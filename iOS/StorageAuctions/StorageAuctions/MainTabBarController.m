//
//  MainTabBarController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/20/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "MainTabBarController.h"
#import "HomeController.h"
#import "AuctionController.h"
#import "SearchController.h"

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
    
    [self setTabMenuIcons];
}

- (void) tabBar:(UITabBar *)tabBar didSelectItem:(UITabBarItem *)item {

    if (item == tabBar.items[0]) {
        [self checkViewControllers:0];
    }
    else if (item == tabBar.items[1]) {
        [self checkViewControllers:1];
    }
}

- (void) checkViewControllers:(NSInteger) index {
    if (index != 0 && [self.viewControllers[0] isKindOfClass:[HomeController class]] == false) {
        HomeController* homeController = (HomeController*) [[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"homeController"];
        
        UITabBarItem* item = [[UITabBarItem alloc] initWithTitle:NULL image:[[UIImage imageNamed:@"home_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic] selectedImage:[[UIImage imageNamed:@"home_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
        item.imageInsets = UIEdgeInsetsMake(6, 0, -6, 0);
        [homeController setTabBarItem:item];
        
        NSArray* array = @[homeController, self.viewControllers[1], self.viewControllers[2]];
        [self setViewControllers:array];
        // [self setSelectedIndex:0];
    }
    
    if (index != 1 && [self.viewControllers[1] isKindOfClass:[AuctionController class]] == false) {
        AuctionController* auctionController = (AuctionController*) [[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"auctionController"];
        
        UITabBarItem* item = [[UITabBarItem alloc] initWithTitle:NULL image:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic] selectedImage:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
        item.imageInsets = UIEdgeInsetsMake(6, 0, -6, 0);
        [auctionController setTabBarItem:item];
        
        NSArray* array = @[self.viewControllers[0], auctionController, self.viewControllers[2]];
        [self setViewControllers:array];
        // [self setSelectedIndex:1];
    }
}


- (void) setTabMenuIcons {
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
