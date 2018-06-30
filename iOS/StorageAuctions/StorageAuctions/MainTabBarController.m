//
//  MainTabBarController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/20/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "MainTabBarController.h"

@interface MainTabBarController ()

@property (weak, nonatomic) IBOutlet UINavigationItem *navigationBar;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *menuBarItem;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *userMenuBarItem;


@end

@implementation MainTabBarController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self.menuBarItem setImage:[[UIImage imageNamed:@"left_top_nav"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    [self.userMenuBarItem setImage:[[UIImage imageNamed:@"user_top_nav"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    self.navigationBar.titleView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"logo_top_nav"]];
    
    UINavigationBar* bar = [self.navigationController navigationBar];
    [bar setBarTintColor:[UIColor colorWithRed:255 / 255.0f green:126 / 255.0f blue:39 / 255.0f alpha:0.8]];
    
    UITabBarItem* homeItem = [[self.tabBar items] objectAtIndex:0];
    [homeItem setImage:[[UIImage imageNamed:@"home_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic]];
    [homeItem setSelectedImage:[[UIImage imageNamed:@"home_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    UITabBarItem* auctionItem = [[self.tabBar items] objectAtIndex:1];
    [auctionItem setImage:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic]];
    [auctionItem setSelectedImage:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
    UITabBarItem* searchItem = [[self.tabBar items] objectAtIndex:2];
    [searchItem setImage:[[UIImage imageNamed:@"search_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic]];
    [searchItem setSelectedImage:[[UIImage imageNamed:@"search_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
