class User < ActiveRecord::Base
	validates :name, {presence: true}
	validates :password, {presence: true}
	validates :time, {presence: true}
end
