class User < ApplicationRecord
  has_many :debts, class_name: "Transaction",
                    foreign_key: "borrower_id",
                    dependent: :destroy
  has_many :lenders, through: :debts

  has_many :credits, class_name: "Transaction",
                    foreign_key: "lender_id",
                    dependent: :destroy
  has_many :borrowers, through: :credits

  validates :name, presence: true, uniqueness: true


  def borrowing?(lender)
    lenders.include? lender
  end

  def lending?(borrower)
    borrow.include? borrower
  end

  # User borrows money from lender
  def borrow!(lender)
    if lender != self && !borrowing?(lender)
      lenders << lender
    end
  end

  # User lends money to borrower
  def lend!(borrower)
    if borrower != self && !lending?(borrower)
      borrowers << borrower
    end
  end


  # Check it's return value
  def owe_summary!
    summary = Hash.new
    debts.each do |debt_transact|
      name = User.where(id: debt_transact.lender_id)
      amount = debt_transact.amount
      summary[name] = amount
    end

    credits.each do |credit_transact|
      name = User.where(id: credit_transact.borrower_id)
      amount = debt_transact.amount * -1
      summary[name] += amount
    end
  end



  end

end
