class Transaction < ApplicationRecord

  belongs_to :borrower, class_name: "User"
  belongs_to :lender, class_name: "User"

  validates_presence_of [:amount]
  validate :valid_ids


  private
    def valid_ids
      if borrower == lender
        errors.add(:borrower, "cannot be the same as lender")
      end
    end

end
