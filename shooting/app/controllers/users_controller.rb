class UsersController < ApplicationController
	protect_from_forgery :except => [:create]
	protect_from_forgery :except => [:create_java]
	def show
		@users = User.all.order(time: :asc).limit(3)
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
	def create_java
		@user = User.new(
			name:params[:name],
			time:params[:time],
			password:params[:password]
		)
		if @user.save
			render json: @user
		else
			render json: @user.errors, status: :unprocessable_entity
		end
	end
end
