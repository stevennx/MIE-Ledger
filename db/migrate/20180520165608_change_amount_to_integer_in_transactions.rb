class ChangeAmountToIntegerInTransactions < ActiveRecord::Migration[5.1]
  def change
    change_column :transactions, :amount, :integer 
  end
end
