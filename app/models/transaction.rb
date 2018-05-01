class Transaction < ApplicationRecord

  belongs_to :borrower, class_name: "User"
  belongs_to :lender, class_name: "User"

  validates_presence_of [:amount, :lender_id, :borrower_id]
end
