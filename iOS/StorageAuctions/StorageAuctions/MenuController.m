//
//  MenuController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/20/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "MenuController.h"
#import "ServiceManager.h"
#import "UIViewController+LGSideMenuController.h"
#import "MainTabBarController.h"

@interface MenuController ()

@end

@implementation MenuController

- (void)viewDidLoad {
    [super viewDidLoad];
    [ServiceManager sharedManager].mainMenuController = self.sideMenuController;
}

- (IBAction)onClickHome:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"Home Clicked");
        UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
        while (topController.presentedViewController) {
            topController = topController.presentedViewController;
        }
        UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
        NSArray* controllerArr = [navController viewControllers];
        MainTabBarController* mainTabController = (MainTabBarController*) controllerArr[0];
        [mainTabController setSelectedIndex:0];
        [navController popToViewController:mainTabController animated:TRUE];
    }];
}

- (IBAction)onClickYourAuctions:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"Your Auctions Clicked");
        UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
        while (topController.presentedViewController) {
            topController = topController.presentedViewController;
        }
        UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
        NSArray* controllerArr = [navController viewControllers];
        MainTabBarController* mainTabController = (MainTabBarController*) controllerArr[0];
        [mainTabController setSelectedIndex:1];
        [navController popToViewController:mainTabController animated:TRUE];
    }];
}

- (IBAction)onListAuction:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"List Auction Clicked");
        UINavigationController* navController = (UINavigationController*) self.sideMenuController.rootViewController;
        UIViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"newAuctionController"];
        [navController pushViewController:controller animated:TRUE];
    }];
}

- (IBAction)onAddNewFacility:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"Add New Facility Clicked");
        UINavigationController* navController = (UINavigationController*) self.sideMenuController.rootViewController;
        UIViewController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"facilityAddressController"];
        [navController pushViewController:controller animated:TRUE];
    }];
}

- (IBAction)onReports:(id)sender {
    [self.sideMenuController hideLeftViewAnimated:TRUE completionHandler:^(void) {
        NSLog(@"Reports Clicked");
    }];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
