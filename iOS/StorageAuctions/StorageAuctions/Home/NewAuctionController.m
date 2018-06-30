//
//  NewAuctionController.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/21/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import "NewAuctionController.h"
#import "ServiceManager.h"
#import "ProgressHUD.h"
#import "InfoPopupController.h"
#import <PopupDialog/PopupDialog-Swift.h>
#import "UIViewController+LGSideMenuController.h"
#import "MainTabBarController.h"

@interface NewAuctionController () <UIPickerViewDelegate, UIPickerViewDataSource>

{
    UIPickerView* facilityPicker;
    UIPickerView* sqFeetPicker;
    UIPickerView* timeZonePicker;
    UIPickerView* cleanOutPicker;
    UIPickerView* accessPicker;

    NSArray* sqFeetArr;
    NSArray* unitSizeArr;
    NSArray* timeZoneArr;
    NSArray* cleanOutArr;
    NSArray* cleanNumArr;
    NSArray* accessArr;
    
    UIDatePicker* datePicker;
    UIDatePicker* timePicker;
    
    NSMutableArray* facilityArr;
    
    NSString* terms;
    NSString* unitSize;
    NSString* cleanNum;
    Facility* selectedFacility;
}


@property (weak, nonatomic) IBOutlet NSLayoutConstraint *scrollViewTopConstraint;

@property (weak, nonatomic) IBOutlet UITextField *facilityName;
@property (weak, nonatomic) IBOutlet UITextField *auctionTitle;
@property (weak, nonatomic) IBOutlet UITextField *realUnit;
@property (weak, nonatomic) IBOutlet UITextField *altUnit;
@property (weak, nonatomic) IBOutlet UITextField *unitSqFeet;
@property (weak, nonatomic) IBOutlet UITextField *cleaningDeposit;
@property (weak, nonatomic) IBOutlet UITextField *lockTagID;
@property (weak, nonatomic) IBOutlet UITextField *tenantName;
@property (weak, nonatomic) IBOutlet UITextField *startDate;
@property (weak, nonatomic) IBOutlet UITextField *startTime;
@property (weak, nonatomic) IBOutlet UITextField *endDate;
@property (weak, nonatomic) IBOutlet UITextField *endTime;
@property (weak, nonatomic) IBOutlet UITextField *timeZone;
@property (weak, nonatomic) IBOutlet UITextField *cleanOut;
@property (weak, nonatomic) IBOutlet UITextField *otherCleanOut;
@property (weak, nonatomic) IBOutlet UITextField *access;


@property (weak, nonatomic) IBOutlet UISwitch *moneyOrder;
@property (weak, nonatomic) IBOutlet UISwitch *creditCard;
@property (weak, nonatomic) IBOutlet UISwitch *check;
@property (weak, nonatomic) IBOutlet UISwitch *otherPayment;
@property (weak, nonatomic) IBOutlet UISwitch *cash;
@property (weak, nonatomic) IBOutlet UITextField *othPaymentField;


@end

@implementation NewAuctionController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.navigationController.navigationBar setTintColor:[UIColor whiteColor]];
    [self.navigationItem setTitle:@"Create New Auction"];
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSForegroundColorAttributeName:[UIColor whiteColor]}];
    
    [self initializeArray];
    [self initializeUI];
}

- (void) initializeUI {
    sqFeetPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
    [sqFeetPicker setDelegate:self];
    [sqFeetPicker setDataSource:self];
    [sqFeetPicker setShowsSelectionIndicator:TRUE];
    [_unitSqFeet setInputView:sqFeetPicker];
    
    timeZonePicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
    [timeZonePicker setDelegate:self];
    [timeZonePicker setDataSource:self];
    [_timeZone setInputView:timeZonePicker];
    [_timeZone setText:timeZoneArr[1]];
    [timeZonePicker selectRow:1 inComponent:0 animated:FALSE];
    
    cleanOutPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
    [cleanOutPicker setDelegate:self];
    [cleanOutPicker setDataSource:self];
    [_cleanOut setInputView:cleanOutPicker];
    [_cleanOut setText:cleanOutArr[0]];
    [_otherCleanOut setEnabled:FALSE];
    cleanNum = cleanNumArr[0];
    [cleanOutPicker selectRow:0 inComponent:0 animated:FALSE];
    
    accessPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
    [accessPicker setDelegate:self];
    [accessPicker setDataSource:self];
    [_access setInputView:accessPicker];
    [_access setText:accessArr[0]];
    [accessPicker selectRow:0 inComponent:0 animated:FALSE];
    
    datePicker = [[UIDatePicker alloc] initWithFrame:CGRectZero];
    [datePicker setDatePickerMode:UIDatePickerModeDate];
    [datePicker addTarget:self action:@selector(onDatePickerValueChanged:) forControlEvents:UIControlEventValueChanged];
    [_startDate setInputView:datePicker];
    [_endDate setInputView:datePicker];
    
    timePicker = [[UIDatePicker alloc] initWithFrame:CGRectZero];
    [timePicker setDatePickerMode:UIDatePickerModeTime];
    [timePicker setMinuteInterval:15];
    [timePicker addTarget:self action:@selector(onTimePickerValueChanged:) forControlEvents:UIControlEventValueChanged];
    [_startTime setInputView:timePicker];
    [_endTime setInputView:timePicker];
    
    [_otherPayment setOn:FALSE];
    [_othPaymentField setEnabled:_otherPayment.isOn];
}

- (void) initializeArray {
    sqFeetArr = [NSArray arrayWithObjects: @"5 × 5", @"6 × 5", @"5 × 10", @"6 × 9", @"6 × 12", @"10 × 10", @"5 × 12", @"8 × 10", @"10 × 12", @"10 × 14",
                  @"12 × 12", @"10 × 15", @"10 × 16", @"10 × 20", @"10 × 24", @"12 × 24", @"20 × 20", @"20 × 25", @"10 × 30", @"30 × 30", nil];
    unitSizeArr = [NSArray arrayWithObjects: @"25", @"30", @"50", @"54", @"72", @"100", @"60", @"80", @"120", @"140",
                   @"144", @"150", @"160", @"200", @"240", @"288", @"400", @"500", @"300", @"900", nil];
    timeZoneArr = [NSArray arrayWithObjects:@"US/Eastern", @"US/Central", @"US/Mountain", @"America/Phoenix", @"US/Pacific", @"America/Anchorage", @"Pacific/Honolulu", nil];
    cleanOutArr = [NSArray arrayWithObjects:@"24 hours", @"48 hours", @"72 hours", @"Other (Specify)", nil];
    cleanNumArr = [NSArray arrayWithObjects:@"24", @"48", @"72", @"0", nil];
    accessArr = [NSArray arrayWithObjects:@"Office Hours", @"Gate Hours", @"24 Hour Access", nil];
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
                facilityPicker = [[UIPickerView alloc] initWithFrame:CGRectZero];
                [facilityPicker setDelegate:self];
                [facilityPicker setDataSource:self];
                [_facilityName setInputView:facilityPicker];
            }
        }];
    }];
}


- (NSInteger) numberOfComponentsInPickerView:(UIPickerView *)pickerView {
    return 1;
}

- (NSInteger) pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    if (pickerView == sqFeetPicker) {
        return sqFeetArr.count;
    } else if (pickerView == facilityPicker) {
        return facilityArr.count;
    } else if (pickerView == timeZonePicker) {
        return timeZoneArr.count;
    } else if (pickerView == cleanOutPicker) {
        return cleanOutArr.count;
    } else if (pickerView == accessPicker) {
        return accessArr.count;
    } else {
        return 0;
    }
}

- (NSString*) pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component {
    if (pickerView == sqFeetPicker) {
        return sqFeetArr[row];
    } else if (pickerView == facilityPicker) {
        Facility* facility = (Facility*) facilityArr[row];
        return facility.name;
    } else if (pickerView == timeZonePicker) {
        return timeZoneArr[row];
    } else if (pickerView == cleanOutPicker) {
        return cleanOutArr[row];
    } else if (pickerView == accessPicker) {
        return accessArr[row];
    } else {
        return @"";
    }
}

- (void) pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component {
    if (pickerView == sqFeetPicker) {
        _unitSqFeet.text = sqFeetArr[row];
        unitSize = unitSizeArr[row];
    } else if (pickerView == timeZonePicker) {
        _timeZone.text = timeZoneArr[row];
    } else if (pickerView == facilityPicker) {
        Facility* facility = (Facility*) facilityArr[row];
        selectedFacility = facility;
        _facilityName.text = facility.name;
    } else if (pickerView == cleanOutPicker) {
        _cleanOut.text = cleanOutArr[row];
        cleanNum = cleanNumArr[row];
        if (row == cleanOutArr.count - 1) {
            [_otherCleanOut setEnabled:TRUE];
        } else {
            [_otherCleanOut setEnabled:FALSE];
        }
    } else if (pickerView == accessPicker) {
        _access.text = accessArr[row];
    }
}

- (void) onDatePickerValueChanged:(UIDatePicker*) picker {
    NSDate* date = [picker date];
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"MM/dd/yyyy"];
    NSString* dateStr = [formatter stringFromDate:date];
    if ([_startDate isFirstResponder]) {
        [_startDate setText:dateStr];
    } else {
        [_endDate setText:dateStr];
    }
}

- (void) onTimePickerValueChanged:(UIDatePicker*) picker {
    NSDate* date = [picker date];
    NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"hh:mm a"];
    [formatter setAMSymbol:@"AM"];
    [formatter setPMSymbol:@"PM"];
    NSString* dateStr = [formatter stringFromDate:date];
    if ([_startTime isFirstResponder]) {
        [_startTime setText:dateStr];
    } else {
        [_endTime setText:dateStr];
    }
}

- (NSString*) getTime:(BOOL) bEndTime {
    NSString* timeStr;
    if (bEndTime) {
        timeStr = [NSString stringWithFormat:@"%@ %@", _endDate.text, _endTime.text];
    } else {
        timeStr = [NSString stringWithFormat:@"%@ %@", _startDate.text, _startTime.text];
    }
    
    NSDateFormatter* formatter1 = [[NSDateFormatter alloc] init];
    [formatter1 setDateFormat:@"MM/dd/yyyy hh:mm a"];
    [formatter1 setAMSymbol:@"AM"];
    [formatter1 setPMSymbol:@"PM"];
    NSDate* date = [formatter1 dateFromString:timeStr];
    
    if (date == nil) {
        return @"";
    }
    
    NSDateFormatter* formatter2 = [[NSDateFormatter alloc] init];
    [formatter2 setDateFormat:@"yyyy-M-d"];
    [formatter2 setDateFormat:@"yyyy-M-d hh:mma"];
    [formatter2 setAMSymbol:@"am"];
    [formatter2 setPMSymbol:@"pm"];
    NSString* retStr = [formatter2 stringFromDate:date];
    return retStr;
}

- (IBAction)onAdditionalInformation:(id)sender {
    InfoPopupController* infoController = (InfoPopupController*)[[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"infoPopupController"];
    infoController.infoTitle = @"Additional Information";
    PopupDialog* dialog = [[PopupDialog alloc] initWithViewController:infoController buttonAlignment:UILayoutConstraintAxisHorizontal transitionStyle:PopupDialogTransitionStyleBounceDown gestureDismissal:TRUE completion:NULL];
    CancelButton* cancelButton = [[CancelButton alloc] initWithTitle:@"CANCEL" dismissOnTap:TRUE action:^(void) {
        
    }];
    DefaultButton* defaultButton = [[DefaultButton alloc] initWithTitle:@"SAVE" dismissOnTap:TRUE action:^(void) {
        NSLog(@"Info Additional Information - %@", infoController.infoTextView.text);
    }];
    [dialog addButtons:@[cancelButton, defaultButton]];
    [self presentViewController:dialog animated:TRUE completion:nil];
}

- (IBAction)onTermsAndConditions:(id)sender {
    InfoPopupController* infoController = (InfoPopupController*)[[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"infoPopupController"];
    infoController.infoTitle = @"Terms and Conditions";
    infoController.detail = terms;
    PopupDialog* dialog = [[PopupDialog alloc] initWithViewController:infoController buttonAlignment:UILayoutConstraintAxisHorizontal transitionStyle:PopupDialogTransitionStyleBounceDown gestureDismissal:TRUE completion:NULL];
    CancelButton* cancelButton = [[CancelButton alloc] initWithTitle:@"CANCEL" dismissOnTap:TRUE action:^(void) {
        
    }];
    DefaultButton* defaultButton = [[DefaultButton alloc] initWithTitle:@"SAVE" dismissOnTap:TRUE action:^(void) {
        NSLog(@"Info Terms and Condition - %@", infoController.infoTextView.text);
        terms = infoController.infoTextView.text;
    }];
    [dialog addButtons:@[cancelButton, defaultButton]];
    [self presentViewController:dialog animated:TRUE completion:nil];
}

- (IBAction)onBidSchedule:(id)sender {
    
}

- (IBAction)onCancelAuction:(id)sender {
    [self.navigationController popViewControllerAnimated:TRUE];
}

- (IBAction)onSave:(id)sender {
    AuctionRequest* request = [[AuctionRequest alloc] init];
    request.amount_owed = @"40";
    request.alt_unit_number = _altUnit.text;
    request.reserve = @"0";
    request.time_end = [self getTime:TRUE];
    request.terms = terms ? terms : @"";
    request.prebid_close = @"0";
    request.lock_tag = _lockTagID.text;
    request.fees = @"40";
    request.batch_email = @"1";
    request.cleanout_other = [_cleanOut.text compare:@"Other (Specify)"] == NSOrderedSame ? _otherCleanOut.text : @"";
    request.payment = @"15";
    request.time_st_zone = _timeZone.text;
    request.cleanout = cleanNum;
    // request.access = _access.text;
    request.access = @"";
    request.auction_type = @"0";
    request.time_start = [self getTime:FALSE];
    request.facility_id = selectedFacility ? [@(selectedFacility.fac_id) stringValue] : @"";
    request.unit_size = unitSize ? unitSize : @"";
    request.descr = _auctionTitle.text;
    request.unit_number = _realUnit.text;
    request.time_end_zone = _timeZone.text;
    request.save_terms = @"0";
    request.payment_other = _otherPayment.isOn ? _othPaymentField.text : @"";
    request.tenant_name = _tenantName.text;
    
    [ProgressHUD show];
    [[ServiceManager sharedManager] createAuction:request completion:^(BOOL bSuccess, NSString* error) {
        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
            if (bSuccess) {
                [ProgressHUD dismiss];
                UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
                while (topController.presentedViewController) {
                    topController = topController.presentedViewController;
                }
                UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
                NSArray* controllerArr = [navController viewControllers];
                MainTabBarController* mainTabController = (MainTabBarController*) controllerArr[0];
                [mainTabController setSelectedIndex:0];
                [navController popToViewController:mainTabController animated:TRUE];
            } else {
                [ProgressHUD showError:error];
            }
        }];
    }];
}

- (IBAction)onPaymentChange:(id)sender {
    if (sender == _otherPayment) {
        [_othPaymentField setEnabled:_otherPayment.isOn];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}

@end
