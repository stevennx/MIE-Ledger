Rails.application.routes.draw do
  # For details on the DSL available within this file, see http://guides.rubyonrails.org/routing.html

  # Hides "api" and "v1" from the url
  scope module: "api" do
    scope module: "v1" do

      resources :users, only: [:index, :create, :destroy] do
        post "/transations/owe", to: "transactions#create_debt"
        post "/transations/lend", to: "transactions#create_credit"
      end

    end
  end


end
