//
//  InfoPopupController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/27/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "InfoPopupController.h"

@interface InfoPopupController ()

@end

@implementation InfoPopupController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [_titleLabel setText:_infoTitle];
    [_infoTextView setText:_detail];
    
    [_infoTextView.layer setBorderColor:[UIColor lightGrayColor].CGColor];
    [_infoTextView.layer setBorderWidth:1];
    [_infoTextView.layer setCornerRadius:4];
    [_infoTextView.layer setMasksToBounds:TRUE];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
