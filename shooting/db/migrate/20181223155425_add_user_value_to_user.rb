class AddUserValueToUser < ActiveRecord::Migration
  def change
    add_column :users, :time, :integer
    add_column :users, :password, :string
  end
end
