class AddUserAllToUser < ActiveRecord::Migration
  def change
    add_column :users, :name, :string
    add_column :users, :password, :string
    add_column :users, :time, :integer
  end
end
