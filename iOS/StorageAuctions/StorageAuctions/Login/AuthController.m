//
//  AuthController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/17/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "AuthController.h"

NSInteger AUTH_VIEW_TAG = 1001;
NSInteger SIGNUP_VIEW_TAG = 2001;
NSInteger SCROLL_VIEW_TAG = 3001;

@interface AuthController ()

{
    BOOL bSelectedSignIn;
    CALayer* buttonLayer;
}

@property (weak, nonatomic) IBOutlet UIButton *signinButton;
@property (weak, nonatomic) IBOutlet UIButton *signupButton;
@property (weak, nonatomic) IBOutlet UIView *signinView;
@property (weak, nonatomic) IBOutlet UIView *signupView;

@end

@implementation AuthController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    bSelectedSignIn = true;
    [self selectButton:self.signinButton selected:true];
    [self changeAuthContainer];
}



- (IBAction)onSignIn:(id)sender {
    bSelectedSignIn = true;
    [self clickButton];
    [self changeAuthContainer];
}

- (IBAction)onSignUp:(id)sender {
    bSelectedSignIn = false;
    [self clickButton];
    [self changeAuthContainer];
}

- (void)clickButton {
    if (bSelectedSignIn == true) {
        [self selectButton:self.signupButton selected:false];
        [self selectButton:self.signinButton selected:true];
    } else {
        [self selectButton:self.signinButton selected:false];
        [self selectButton:self.signupButton selected:true];
    }
}

- (void)changeAuthContainer {
    if (bSelectedSignIn == true) {
        [self.signinView setHidden:false];
        [self.signupView setHidden:true];
    } else {
        [self.signinView setHidden:true];
        [self.signupView setHidden:false];
        UIScrollView* scrollView = NULL;
        UIView* signupSubView = [self.signupView.subviews objectAtIndex:0];
        for (NSInteger i = 0; i < signupSubView.subviews.count; i++) {
            UIView* subView = [signupSubView.subviews objectAtIndex:i];
            if (subView.tag == SCROLL_VIEW_TAG) {
                scrollView = (UIScrollView*) subView;
                break;
            }
        }
        if (scrollView != NULL) {
            [scrollView setContentOffset:CGPointZero animated:true];
        }
    }
}

- (void)selectButton:(UIButton*) button selected:(BOOL) bSelected {
    if (bSelected) {
        buttonLayer = [[CALayer alloc] init];
        [buttonLayer setBackgroundColor:[UIColor colorWithRed:234 / 255.0f green:128 / 255.0f blue:72 / 255.0f alpha:1].CGColor];
        [buttonLayer setFrame:CGRectMake(0, button.frame.size.height - 1, button.frame.size.width, 1)];
        [button.layer addSublayer:buttonLayer];
    } else {
        if (buttonLayer != NULL) {
            [buttonLayer removeFromSuperlayer];
        }
    }
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
