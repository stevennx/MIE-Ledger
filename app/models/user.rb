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


  def add_hash(summary, name, amount)
    if summary[name]
      summary[name] += amount
    else
      summary[name] = amount
    end
  end

  def owe_summary
    summary = Hash.new

    debts.each do |debt_transact|
      name = debt_transact.lender.name
      amount = debt_transact.amount
      add_hash(summary, name, amount)
    end

    credits.each do |credit_transact|
      name = credit_transact.borrower.name
      amount = -credit_transact.amount
      add_hash(summary, name, amount)
    end

    summary.each do |key, value|
      if value == 0
        summary.delete(key)
      end
    end

    return summary
  end
end
