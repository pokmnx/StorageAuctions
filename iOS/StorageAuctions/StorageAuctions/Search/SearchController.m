//
//  SearchController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/20/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "SearchController.h"
#import "ServiceManager.h"
#import "ProgressHUD.h"
#import "FacilityCell.h"
@import GoogleMaps;


@interface SearchController () <UITableViewDataSource, UITableViewDelegate>

{
    NSMutableArray* facilityArr;
}
@property (weak, nonatomic) IBOutlet UITableView *facilityListView;

@end

@implementation SearchController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    [ProgressHUD show];
    [[ServiceManager sharedManager] getAllFacilities:^(NSArray* facArr) {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            [ProgressHUD dismiss];
            if (facArr == NULL) {
                [ProgressHUD showError:@"Couldn't load Facilities"];
            } else {
                facilityArr = [NSMutableArray arrayWithArray:facArr];
                [self.facilityListView reloadData];
            }
        }];
    }];
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return facilityArr.count;
}

- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    FacilityCell* cell = (FacilityCell*) [tableView dequeueReusableCellWithIdentifier:@"facilityCell" forIndexPath:indexPath];
    Facility* facility = facilityArr[indexPath.row];
    
    cell.facilityName.text = facility.name;
    cell.facilityAddress.text = [NSString stringWithFormat:@"%@ %@ %@", facility.address, facility.state_code, facility.postal_code];
    cell.activeAuctions.text = [NSString stringWithFormat:@"Active Auctions: %ld", (long)facility.active];
    cell.lat = facility.lat;
    cell.lon = facility.lon;
    
    return cell;
}

- (CGFloat) tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 200;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
}

@end
