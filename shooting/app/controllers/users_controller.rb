class UsersController < ApplicationController
	def show
		@users = User.all
	end
	def new
		@user = User.new
	end
	def create
		@user = User.new(
			name:params[:name],
			time:params[:time],
			password:params[:password]
		)
		if @user.save
			flash[:notice] = "登録しました。"
			redirect_to("/users")
		else
			flash[:notice] = "登録できませんでした。"
			render("users/new")
		end


	end
end
