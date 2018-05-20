class Api::V1::TransactionsController < ApplicationController
  before_action :set_user, except: [:index, :active, :set_extra_params]

  def index
    @transactions = Transaction.all
    render json: @transactions
  end

  def debts
    @debts = @user.debts
    render json: @debts
  end

  def credits
    @credits = @user.credits
    render json: @credits
  end

  def active
    @transaction = Transaction.find(active_params[:transaction_id])
    @transaction.active = active_params[:active]
    Transaction.all.sort()
    if @transaction.save
      render json: { status: 200,
                    messsage: "Successfully set active to #{ @transaction.active }" }
    else
      render json: { status: 500, errors: @transaction.errors }
    end
  end

  def set_extra_params
    Transaction.all.each do |transaction|
      transaction.active = true
      transaction.description = "We got scammed"
      if !transaction.save
        render json: { status: 500, errors: transaction.errors }
      end
    end
  end

  def create_debt
    @transaction = Transaction.new(debt_params)
    @transaction.amount = @transaction.amount.round

    # Initialize lenders, borrowers and active status
    init_transaction(@transaction, @user, User.find(debt_params[:lender_id]))
    if @transaction.save && @transaction.amount > 0
      render json: { status: 200,
                    messsage: "Successfully created more debt!" }
    elsif @transaction.amount < 0
      render json: { status: 200,
                    messsage: "Successfully made payment!" }
    else
      render json: { status: 500, errors: @transaction.errors }
    end
  end

=begin
def create_credit
  @transaction = Transaction.new(credit_params)
  @transaction.lender = @user
  @transaction.borrower = User.find(credit_params[:borrower_id])
  if @transaction.save
    render json: { status: 200,
                  messsage: "Successfully created earned more money!" }
  else
    render json: { status: 500, errors: @transaction.errors }
  end
end
=end

  private

    def init_transaction(transaction, user, lender)
      transaction.borrower = user
      transaction.lender = lender
      transaction.active = true
    end

    def debt_params
      params.permit(:lender_id, :amount, :description)
    end

    def active_params
      params.permit(:active, :transaction_id)
    end

    #def credit_params
    #  params.permit(:borrower_id, :amount)
    #end

    def set_user
      @user = User.find(params[:user_id])
    end

end
