class CreateTransactions < ActiveRecord::Migration[5.1]
  def change
    create_table :transactions do |t|
      t.references :borrower, foreign_key: true
      t.references :lender, foreign_key: true
      t.integer :amount

      t.timestamps
    end
  end
end
