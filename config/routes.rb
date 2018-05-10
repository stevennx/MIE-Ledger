Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

  # Hides "api" and "v1" from the url
  scope module: "api" do
    scope module: "v1" do

      resources :users, only: [:index, :show, :create, :destroy] do
        post "owe", to: "transactions#create_debt"
        get "debts", to: "transactions#debts"
        get "credits", to: "transactions#credits"
        get "borrowers", to: "users#borrowers"
        get "lenders", to: "users#lenders"
        get "summary", to: "users#summary"
      end


      get "transactions", to: "transactions#index"
      post "transactions/active", to: "transactions#active"
      patch "set_params", to: "transactions#set_extra_params"
    end
  end


end
