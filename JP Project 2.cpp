/*
 * The code below is meant to detect whether the Red Ruby
 * is present. The starategy implemented by the code is not very effective. 
 * Study the code so that you understand what the strategy is and how 
 * it is implemented. Then design and implement a better startegy.
 * 
 * */


#include <algorithm>
#include <iostream>
#include "E101.h"
#include <vector>

using namespace std;

int main() {
  int err = init(0);
  cout<<"Error: "<<err<<endl;
  open_screen_stream();
  int start_left_most_col;
  int start_top_most_row;
  int start_right_most_col;
  int start_bottom_most_row;
  for (int countrun = 0; countrun < 10000; countrun++) {
	take_picture();
	update_screen();
	int totRedPix = 0;
	int totWhitePix = 0;
	
	std::vector<int> red_cols;
	red_cols.clear();
	
	std::vector<int> red_rows;
	red_rows.clear();

 
    for (int row = 0 ; row < 240 ; row++) 
		{
		for (int col = 0; col < 320; col++) 
			{
			
		if (((get_pixel(row,col,0) / (get_pixel(row,col,1) + 0.0001))>1.5) && (get_pixel(row,col,0) / (get_pixel(row,col,2) + 0.0001))>1.5)
				{
		totRedPix++;
		red_cols.push_back(col);
		red_rows.push_back(row);
		}
		else 
		{
		totWhitePix++;
		}
	}
}		
	
	if (totRedPix > 0) 
	{
		
		
	if (countrun == 0) 
	{
	start_left_most_col = *std::min_element(red_cols.begin(),red_cols.end());
	start_top_most_row = *std::min_element(red_rows.begin(),red_rows.end());
	start_right_most_col = *std::max_element(red_cols.begin(),red_cols.end());
	start_bottom_most_row = *std::max_element(red_rows.begin(),red_rows.end());
	}

	int current_left_most_col = *std::min_element(red_cols.begin(), red_cols.end());
	std::cout<<"Current left most column = "<<current_left_most_col<<std::endl;
	std::cout<<"Start left most column = "<<start_left_most_col<<std::endl;
	
	int current_top_most_row = *std::min_element(red_rows.begin(), red_rows.end());
	std::cout<<"Current top most row = "<<current_top_most_row<<std::endl;
	std::cout<<"Start top most row = "<<start_top_most_row<<std::endl;
	
	int current_right_most_col = *std::max_element(red_cols.begin(), red_cols.end());
	std::cout<<"Current right most column = "<<current_right_most_col<<std::endl;
	std::cout<<"Start right most column = "<<start_right_most_col<<std::endl;
	
	int current_bottom_most_row = *std::max_element(red_rows.begin(), red_rows.end());
	std::cout<<"Current bottom most row = "<<current_bottom_most_row<<std::endl;
	std::cout<<"Start bottom most row = "<<start_bottom_most_row<<std::endl;
	
	std::cout<<"Total Red Pixels: "<<totRedPix<<std::endl;
	std::cout<<"Total White Pixels: "<<totWhitePix<<std::endl;
	
	if (((current_left_most_col - start_left_most_col) > 5) || ((current_left_most_col - start_left_most_col) < -5)) 
	{
		std::cout<<"The Ruby has been tampered with"<<std::endl;
		break;
	}
	if (((current_top_most_row - start_top_most_row) > 5) || ((current_top_most_row - start_top_most_row) < -5)) 
	{
		std::cout<<"The Ruby has been tampered with"<<std::endl;
		break;
	}
	if (((current_right_most_col - start_right_most_col) > 5) || ((current_right_most_col - start_right_most_col) < -5)) 
	{
		std::cout<<"The Ruby has been tampered with"<<std::endl;
		break;
	}
	if (((current_bottom_most_row - start_bottom_most_row) > 5) || ((current_bottom_most_row - start_bottom_most_row) < -5)) 
	{
		std::cout<<"The Ruby has been tampered with"<<std::endl;
		break;
	}
	else 
	{
		std::cout<<"The Ruby is secure"<<std::endl;
	}
}

	else 
	{
		std::cout<<"The Ruby has been stolen"<<std::endl;
		break;
}

	cout<<" Picture: "<<countrun<<endl<<endl;
	
	sleep1(0.0000000000001); // slow down a bit to make display easier
	
  }  
  close_screen_stream();
  return 0;
}
