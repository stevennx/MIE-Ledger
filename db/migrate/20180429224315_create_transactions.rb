class CreateTransactions < ActiveRecord::Migration[5.1]
  def change
    create_table :transactions do |t|
      t.integer :borrower_id
      t.integer :lender_id
      t.integer :amount

      t.timestamps
    end

    add_foreign_key :transactions, :users, :column :borrower_id
    add_index :transactions, :borrower_id
    add_foreign_key :transactions, :users, :column :lender_id
    add_index :transactions, :lender_id

  end
end
