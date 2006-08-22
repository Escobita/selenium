class CreateRuns < ActiveRecord::Migration
  def self.up
    create_table :runs do |t|
      t.column :time, :timestamp
      t.column :version, :string
      t.column :revision, :string
      t.column :total_time, :float
      t.column :note, :text
    end
  end

  def self.down
    drop_table :runs
  end
end
