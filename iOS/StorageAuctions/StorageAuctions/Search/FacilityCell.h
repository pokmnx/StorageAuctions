//
//  FacilityCell.h
//  StorageAuctions
//
//  Created by PSIHPOK on 6/22/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FacilityCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel *facilityName;
@property (weak, nonatomic) IBOutlet UILabel *facilityAddress;
@property (weak, nonatomic) IBOutlet UILabel *activeAuctions;
@property (weak, nonatomic) IBOutlet UILabel *totalAuctions;
@property (weak, nonatomic) IBOutlet UIView *mapContainer;
@property (nonatomic) CGFloat lat;
@property (nonatomic) CGFloat lon;

@end
