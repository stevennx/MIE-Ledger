class Api::V1::TransactionsController < ApplicationController
  before_action :set_user, except: [:index]

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

  def create_debt
    @transaction = Transaction.new(debt_params)
    @transaction.borrower = @user
    @transaction.lender = User.find(debt_params[:lender_id])
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

  private
    def debt_params
      params.permit(:lender_id, :amount)
    end

    def credit_params
      params.permit(:borrower_id, :amount)
    end

    def set_user
      @user = User.find(params[:user_id])
    end

end
