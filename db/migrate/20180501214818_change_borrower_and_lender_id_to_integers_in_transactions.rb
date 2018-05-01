class ChangeBorrowerAndLenderIdToIntegersInTransactions < ActiveRecord::Migration[5.1]
  def change
    change_column :transactions, :borrower_id, :integer
    change_column :transactions, :lender_id, :integer
  end
end
