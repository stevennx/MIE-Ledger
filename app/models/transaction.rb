class Transaction < ApplicationRecord

  belongs_to :borrower, class_name: "User"
  belongs_to :lender, class_name: "User"

  validates_presence_of [:amount]
  validate :valid_ids
  validate :not_zero


  private
    def valid_ids
      if borrower == lender
        errors.add(:borrower, "cannot be the same as lender")
      end
    end

    def not_zero
      if amount == 0
        errors.add(:amount, "cannot be zero")
      end
    end

end
