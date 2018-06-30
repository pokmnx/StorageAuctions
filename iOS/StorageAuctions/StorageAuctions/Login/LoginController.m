//
//  LoginController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/17/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "LoginController.h"
#import "ProgressHUD.h"
#import "ServiceManager.h"
#import "AppDelegate.h"

@interface LoginController ()

@property (weak, nonatomic) IBOutlet UITextField *emailField;
@property (weak, nonatomic) IBOutlet UITextField *passwordField;


@end

@implementation LoginController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [_emailField setText:@"ro.bert.miner@gmail.com"];
    [_passwordField setText:@"miner001"];
}

- (IBAction)onLogin:(id)sender {
    [ProgressHUD show];
    [[ServiceManager sharedManager] loginWith:self.emailField.text password:self.passwordField.text completion:^(BOOL bSuccess, NSString* error) {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            if (bSuccess == true) {
                [ProgressHUD dismiss];
                UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
                while (topController.presentedViewController) {
                    topController = topController.presentedViewController;
                }
                [topController performSegueWithIdentifier:@"loginSuccess" sender:NULL];
            } else {
                [ProgressHUD showError:error];
            }
        }];
    }];
}

- (IBAction)onForgotPassword:(id)sender {
    
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
