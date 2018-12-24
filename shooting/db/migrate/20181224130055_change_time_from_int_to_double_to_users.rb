class ChangeTimeFromIntToDoubleToUsers < ActiveRecord::Migration
  def change
    remove_column :users, :time, :integer
    add_column :users, :time, :double
  end
end
