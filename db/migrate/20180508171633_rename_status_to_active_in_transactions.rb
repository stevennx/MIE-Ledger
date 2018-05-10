class RenameStatusToActiveInTransactions < ActiveRecord::Migration[5.1]
  def change
    rename_column :transactions, :status, :active
  end
end
