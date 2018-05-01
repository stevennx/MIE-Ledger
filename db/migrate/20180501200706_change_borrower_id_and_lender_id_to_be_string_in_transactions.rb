class ChangeBorrowerIdAndLenderIdToBeStringInTransactions < ActiveRecord::Migration[5.1]
  def change
    change_column :transactions, :borrower_id, :string
    change_column :transactions, :lender_id, :string
  end
end
