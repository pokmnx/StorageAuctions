//
//  InfoPopupController.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/27/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface InfoPopupController : UIViewController



@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UITextView *infoTextView;

@property (nonatomic) NSString* infoTitle;
@property (nonatomic) NSString* detail;

@end
