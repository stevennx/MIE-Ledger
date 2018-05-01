class TransactionsController < ApplicationController
  before_action :set_user
  #before_action :set_transaction

    def create_debt
      @transaction = Transaction.new(debt_params)
      @transaction.borrower_id = @user.id
      @user.borrow!(debt_params[:lender_id])
      if @transaction.save
        render json: { status: 200,
                      messsage: "Successfully created more debt!" }
      else
        render json: { status: 500, errors: @transaction.errors }
      end
    end

    def create_credit
      @transaction = Transaction.new(credit_params)
      @transaction.lender_id = @user.id
      @user.lend!(credit_params[:borrower_id])
      if @transaction.save
        render json: { status: 200,
                      messsage: "Successfully created more credit!" }
      else
        render json: { status: 500, errors: @transaction.errors }
      end
    end

    def

    private
      def debt_params
        params.permit(:lender_id, :amount)
      end

      def credit_params
        params.permit(:borrower_id, :amount)
      end

      def set_transaction
        @transaction = Transaction.find(params[:id])
      end

      def set_user
        @user = User.find(params[:user_id])
      end

end
