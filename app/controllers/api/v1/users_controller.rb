class Api::V1::UsersController < ApplicationController
  before_action :set_user, only: [:destroy, :show, :borrowers, :lenders]

  def index
    @users = User.all
    render json: @users
  end

  def borrowers
    render json: @user.borrowers
  end

  def lenders
    render json: @user.lenders
  end


  def summary
    render json: @user.owe_summary!
  end


  def create
    @user = User.new(user_params)
    if @user.save
      render json: { status: 200, messsage: "Successfully created user" }
    else
      render json: { status: 500, errors: @user.errors }
    end
  end

  def destroy
    @user.destroy
    head :no_content
  end

  private

    def user_params
      params.permit(:name)
    end

    def set_user
      @user = User.find(params[:id])
    end
end
