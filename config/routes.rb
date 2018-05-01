Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

  # Hides "api" and "v1" from the url
  scope module: "api" do
    scope module: "v1" do

      resources :users, only: [:index, :create, :destroy] do
        post "transactions/owe", to: "transactions#create_debt"
        post "transactions/lend", to: "transactions#create_credit"
        get "borrowers", to: "users#borrowers"
        get "lenders", to: "users#lenders"
        get "summary", to: "users#summary"
      end

      get "transactions", to: "transactions#index"

    end
  end


end
