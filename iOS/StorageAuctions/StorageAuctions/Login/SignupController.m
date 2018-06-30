//
//  SignupController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/17/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "SignupController.h"
#import "ProgressHUD.h"
#import "ServiceManager.h"

@interface SignupController ()

{
    BOOL bSelectAuction;
}

@property (weak, nonatomic) IBOutlet UIButton *auctionSelectBtn;
@property (weak, nonatomic) IBOutlet UIButton *ownerSelectBtn;

@property (weak, nonatomic) IBOutlet UITextField *usernameField;
@property (weak, nonatomic) IBOutlet UITextField *emailField;
@property (weak, nonatomic) IBOutlet UITextField *passwordField;
@property (weak, nonatomic) IBOutlet UITextField *nameField;
@property (weak, nonatomic) IBOutlet UITextField *phoneField;


@end

@implementation SignupController

- (void)viewDidLoad {
    [super viewDidLoad];
    bSelectAuction = true;
    [self.auctionSelectBtn setImage:[UIImage imageNamed:@"choose_select"] forState:UIControlStateNormal];
    [self.ownerSelectBtn setImage:[UIImage imageNamed:@"choose_empty"] forState:UIControlStateNormal];
}

- (IBAction)onSelectAuction:(id)sender {
    bSelectAuction = true;
    [self.auctionSelectBtn setImage:[UIImage imageNamed:@"choose_select"] forState:UIControlStateNormal];
    [self.ownerSelectBtn setImage:[UIImage imageNamed:@"choose_empty"] forState:UIControlStateNormal];
}

- (IBAction)onSelectOwner:(id)sender {
    bSelectAuction = false;
    [self.auctionSelectBtn setImage:[UIImage imageNamed:@"choose_empty"] forState:UIControlStateNormal];
    [self.ownerSelectBtn setImage:[UIImage imageNamed:@"choose_select"] forState:UIControlStateNormal];
}

- (IBAction)onSignUp:(id)sender {
    NSString* role = bSelectAuction ? @"a" : @"b";
    [ProgressHUD show];
    [[ServiceManager sharedManager] signupWith:self.emailField.text password:self.passwordField.text username:self.usernameField.text phone:self.phoneField.text name:self.nameField.text role:role completion:^(BOOL bSuccess, NSString* error) {
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


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
