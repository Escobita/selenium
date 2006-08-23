class CreateTestCases < ActiveRecord::Migration
  def self.up
    create_table :test_cases do |t|
      t.column :run_id, :integer
      t.column :name, :string
      t.column :status, :string
      t.column :note, :text
    end
  end

  def self.down
    drop_table :test_cases
  end
end
