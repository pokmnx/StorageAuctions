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
#import "AuctionDetailController.h"
#import "HomeController.h"
#import "AuctionController.h"
#import "SearchController.h"
#import "DateTools.h"

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
    NSString* infoText;
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
@property (weak, nonatomic) IBOutlet UITextField *startingPrice;


@property (weak, nonatomic) IBOutlet UISwitch *moneyOrder;
@property (weak, nonatomic) IBOutlet UISwitch *creditCard;
@property (weak, nonatomic) IBOutlet UISwitch *check;
@property (weak, nonatomic) IBOutlet UISwitch *otherPayment;
@property (weak, nonatomic) IBOutlet UISwitch *cash;
@property (weak, nonatomic) IBOutlet UITextField *othPaymentField;

@property (weak, nonatomic) IBOutlet UIButton *cancelButton;



@end

@implementation NewAuctionController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    [self.navigationController.navigationBar setTintColor:[UIColor whiteColor]];
    [self.navigationController.navigationBar setTitleTextAttributes:
     @{NSForegroundColorAttributeName:[UIColor whiteColor]}];
    
    [self initializeArray];
    [self initializeUI];
    [self initWithAuction];
}

- (void) initWithAuction {
    if (_bEdit && _auction != NULL) {
        _facilityName.text = _auction.facility_name;
        _auctionTitle.text = _auction.title;
        _realUnit.text = _auction.unit_number;
        _altUnit.text = _auction.alt_unit_number;
        _startingPrice.text = [NSString stringWithFormat:@"%g", _auction.reserve];
        _cleaningDeposit.text = [NSString stringWithFormat:@"%g", _auction.fees];
        
        if (_auction.status == 6 || _auction.status == 7 || _auction.status == 8) {
            [_cancelButton setEnabled:false];
        }
    }
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    if (_bEdit && _auction != NULL) {
        [self.navigationItem setTitle:@"Edit Auction"];
    } else {
        [self.navigationItem setTitle:@"Create New Auction"];
    }
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.navigationItem setTitle:@""];
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
    
    if (_bEdit == true && _auction != NULL) {
        NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"MM/dd/yyyy hh:mm a"];
        formatter.AMSymbol = @"AM";
        formatter.PMSymbol = @"PM";
        
        int timeBefore = [_auction.time_st_zone characterAtIndex:_auction.time_st_zone.length - 1] - 48;
        NSDate* startDate = [_auction.time_start dateBySubtractingHours:timeBefore];
        
        NSString* dateStr = [formatter stringFromDate:startDate];
        _startDate.text = [dateStr componentsSeparatedByString:@" "][0];
        _startTime.text = [NSString stringWithFormat:@"%@ %@", [dateStr componentsSeparatedByString:@" "][1], [dateStr componentsSeparatedByString:@" "][2]];
        
        NSDate* endDate = [_auction.time_end dateBySubtractingHours:timeBefore];
        dateStr = [formatter stringFromDate:endDate];
        _endDate.text = [dateStr componentsSeparatedByString:@" "][0];
        _endTime.text = [NSString stringWithFormat:@"%@ %@", [dateStr componentsSeparatedByString:@" "][1], [dateStr componentsSeparatedByString:@" "][2]];
        
        _timeZone.text = _auction.time_st_zone;
        if (_auction.meta != NULL) {
            switch (_auction.meta.cleanout) {
                case 0:
                    _cleanOut.text = cleanOutArr[3];
                    if ([_auction.meta.cleanout_other compare:@"<null>"] != NSOrderedSame)
                        _otherCleanOut.text = _auction.meta.cleanout_other;
                    break;
                case 24:
                    _cleanOut.text = cleanOutArr[0];
                    break;
                case 48:
                    _cleanOut.text = cleanOutArr[1];
                    break;
                case 72:
                    _cleanOut.text = cleanOutArr[2];
                    break;
                default:
                    break;
            }
            
            NSInteger payment = _auction.meta.payment;
            [_cash setOn:((payment % 2 == 1) ? true : false) animated:false];
            payment = payment / 2;
            [_creditCard setOn:((payment % 2 == 1) ? true : false) animated:false];
            payment = payment / 2;
            [_moneyOrder setOn:((payment % 2 == 1) ? true : false) animated:false];
            payment = payment / 2;
            [_check setOn:((payment % 2 == 1) ? true : false) animated:false];
            payment = payment / 2;
            
            if (_auction.meta.payment_other != NULL && _auction.meta.payment_other != [NSNull null] && [_auction.meta.payment_other compare:@"<null>"] != NSOrderedSame) {
                [_otherPayment setOn:true animated:false];
                _othPaymentField.text = _auction.meta.payment_other;
            }
        }
        
        
        if (_auction.tenant_name != [NSNull null] && [_auction.tenant_name compare:@"<null>"] != NSOrderedSame) {
            _tenantName.text = _auction.tenant_name;
        }
        
        if (_auction.terms != [NSNull null] && [_auction.terms compare:@"<null>"] != NSOrderedSame) {
            terms = _auction.terms;
        }
    } else {
        NSDateFormatter* formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"MM/dd/yyyy"];
        NSString* dateStr = [formatter stringFromDate:[self getNextDate:[NSDate date] count:1]];
        _startDate.text = dateStr;
        _startTime.text = @"12:00 PM";
        
        _endDate.text = [formatter stringFromDate:[self getNextDate:[NSDate date] count:7]];
        _endTime.text = @"12:00 PM";
    }
}

- (NSDate*) getNextDate:(NSDate*) date count:(NSInteger) count {
    NSCalendar *gregorian = [[NSCalendar alloc] initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    NSDateComponents *todayComponents = [gregorian components:(NSCalendarUnitDay | NSCalendarUnitMonth | NSCalendarUnitYear) fromDate:date];
    NSInteger theDay = [todayComponents day];
    NSInteger theMonth = [todayComponents month];
    NSInteger theYear = [todayComponents year];
    
    // now build a NSDate object for yourDate using these components
    NSDateComponents *components = [[NSDateComponents alloc] init];
    [components setDay:theDay];
    [components setMonth:theMonth];
    [components setYear:theYear];
    NSDate *thisDate = [gregorian dateFromComponents:components];
    
    // now build a NSDate object for the next day
    NSDateComponents *offsetComponents = [[NSDateComponents alloc] init];
    [offsetComponents setDay:count];
    NSDate *nextDate = [gregorian dateByAddingComponents:offsetComponents toDate:thisDate options:0];
    return nextDate;
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
                
                if (_bEdit && _auction != NULL) {
                    _facilityName.text = _auction.facility_name;
                    for (NSInteger index = 0; index < facArr.count; index++) {
                        Facility* facility = (Facility*) facArr[index];
                        if ([_auction.facility_name compare:facility.name] == NSOrderedSame) {
                            [facilityPicker selectRow:index inComponent:0 animated:false];
                            selectedFacility = facility;
                            break;
                        }
                    }
                    
                    NSString* uSize = [NSString stringWithFormat:@"%ld", _auction.unit_size];
                    for (NSInteger index = 0; index < unitSizeArr.count; index++) {
                        if ([uSize compare:unitSizeArr[index]] == NSOrderedSame) {
                            [_unitSqFeet setText:sqFeetArr[index]];
                            [sqFeetPicker selectRow:index inComponent:0 animated:false];
                            unitSize = unitSizeArr[index];
                            break;
                        }
                    }
                }
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
    infoController.infoTextView.text = infoText;
    PopupDialog* dialog = [[PopupDialog alloc] initWithViewController:infoController buttonAlignment:UILayoutConstraintAxisHorizontal transitionStyle:PopupDialogTransitionStyleBounceDown gestureDismissal:TRUE completion:NULL];
    CancelButton* cancelButton = [[CancelButton alloc] initWithTitle:@"CANCEL" dismissOnTap:TRUE action:^(void) {
        
    }];
    DefaultButton* defaultButton = [[DefaultButton alloc] initWithTitle:@"SAVE" dismissOnTap:TRUE action:^(void) {
        NSLog(@"Info Additional Information - %@", infoController.infoTextView.text);
        infoText = infoController.infoTextView.text;
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
    if (_bEdit && _auction != NULL) {
        AuctionRequest* request = [[AuctionRequest alloc] init];
        request.amount_owed = @"40";
        request.alt_unit_number = _altUnit.text;
        request.reserve = _startingPrice.text;
        request.time_end = [self getTime:TRUE];
        request.terms = terms ? terms : @"";
        request.prebid_close = @"0";
        request.lock_tag = _lockTagID.text;
        request.fees = _cleaningDeposit.text;
        request.batch_email = @"1";
        request.cleanout_other = [_cleanOut.text compare:@"Other (Specify)"] == NSOrderedSame ? _otherCleanOut.text : @"";
        request.payment = [self getPayment];
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
        request.auct_id = [NSString stringWithFormat:@"%ld", _auction.auct_id];
        request.status = @"6";
        
        [ProgressHUD show];
        
        [[ServiceManager sharedManager] setAuction:request completion:^(BOOL bSuccess, Auction* auction, NSString* error) {
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
                    
                    
                    if ([mainTabController.viewControllers[1] isKindOfClass:[AuctionController class]] == false) {
                        AuctionController* auctionController = (AuctionController*) [[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"auctionController"];
                        
                        UITabBarItem* item = [[UITabBarItem alloc] initWithTitle:NULL image:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic] selectedImage:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
                        item.imageInsets = UIEdgeInsetsMake(6, 0, -6, 0);
                        [auctionController setTabBarItem:item];
                        
                        NSArray* array = @[mainTabController.viewControllers[0], auctionController, mainTabController.viewControllers[2]];
                        [mainTabController setViewControllers:array];
                    }
                    
                    [mainTabController setSelectedIndex:1];
                    [navController popToViewController:mainTabController animated:TRUE];
                } else {
                    [ProgressHUD showError:error];
                }
            }];
        }];
    } else {
        [self.navigationController popViewControllerAnimated:TRUE];
    }
}

- (NSString*) getPayment {
    int payment = 0;
    payment = _cash.isOn ? 1 : 0;
    payment = payment * 2 + (_creditCard.isOn ? 1 : 0);
    payment = payment * 2 + (_moneyOrder.isOn ? 1 : 0);
    payment = payment * 2 + (_check.isOn ? 1 : 0);
    return [NSString stringWithFormat:@"%d", payment];
}

- (IBAction)onSave:(id)sender {

    AuctionRequest* request = [[AuctionRequest alloc] init];
    request.amount_owed = @"40";
    request.alt_unit_number = _altUnit.text;
    request.reserve = _startingPrice.text;
    request.time_end = [self getTime:TRUE];
    request.terms = terms ? terms : @"";
    request.prebid_close = @"0";
    request.lock_tag = _lockTagID.text;
    request.fees = _cleaningDeposit.text;
    request.batch_email = @"1";
    request.cleanout_other = [_cleanOut.text compare:@"Other (Specify)"] == NSOrderedSame ? _otherCleanOut.text : @"";
    
    request.payment = [self getPayment];
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
    if (_bEdit && _auction != NULL) {
        request.auct_id = [NSString stringWithFormat:@"%ld", _auction.auct_id];
        [[ServiceManager sharedManager] setAuction:request completion:^(BOOL bSuccess, Auction* auction, NSString* error) {
            [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                if (bSuccess) {
                    [ProgressHUD dismiss];
                    UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
                    while (topController.presentedViewController) {
                        topController = topController.presentedViewController;
                    }
                    UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
                    NSArray* controllerArr = [navController viewControllers];
                    MainTabBarController* mainTabController = NULL;
                     
                    for (NSInteger index = 0; index < controllerArr.count; index++) {
                        if ([controllerArr[index] isKindOfClass:[MainTabBarController class]]) {
                            mainTabController = (MainTabBarController*) controllerArr[index];
                            break;
                        }
                    }
                     
                    if (mainTabController != NULL) {
                        [mainTabController setSelectedIndex:1];
                        [navController popToViewController:mainTabController animated:TRUE];
                    }
                } else {
                    [ProgressHUD showError:error];
                }
            }];
        }];
    } else {
        [[ServiceManager sharedManager] createAuction:request completion:^(BOOL bSuccess, Auction* auction, NSString* error) {
                if (bSuccess) {
                    [[ServiceManager sharedManager] getAuctionDetail:auction.auct_id completion:^(BOOL bSuccess, Auction* auction, NSString* error) {
                        [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                            [ProgressHUD dismiss];
                            if (bSuccess) {
                                UIViewController *topController = [UIApplication sharedApplication].keyWindow.rootViewController;
                                while (topController.presentedViewController) {
                                    topController = topController.presentedViewController;
                                }
                                UINavigationController* navController = (UINavigationController*) ((LGSideMenuController*)topController).rootViewController;
                                NSArray* controllerArr = [navController viewControllers];
                                MainTabBarController* mainTabController = NULL;
                                
                                for (NSInteger index = 0; index < controllerArr.count; index++) {
                                    if ([controllerArr[index] isKindOfClass:[MainTabBarController class]]) {
                                        mainTabController = (MainTabBarController*) controllerArr[index];
                                        break;
                                    }
                                }
                                
                                if (mainTabController != NULL) {
                                    AuctionDetailController* detailController = (AuctionDetailController*)[[UIStoryboard storyboardWithName:@"Main" bundle:NULL] instantiateViewControllerWithIdentifier:@"auctionDetailController"];
                                    
                                    UITabBarItem* item = [[UITabBarItem alloc] initWithTitle:NULL image:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAutomatic] selectedImage:[[UIImage imageNamed:@"auction_tab"] imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
                                    item.imageInsets = UIEdgeInsetsMake(6, 0, -6, 0);
                                    [detailController setTabBarItem:item];
                                    
                                    detailController.auction = auction;
                                    
                                    NSArray* array = @[mainTabController.viewControllers[0], detailController, mainTabController.viewControllers[2]];
                                    [mainTabController setViewControllers:array];
                                    [mainTabController setSelectedIndex:1];
                                    [navController popToViewController:mainTabController animated:TRUE];
                                }
                            } else {
                                [ProgressHUD showError:error];
                            }
                        }];
                    }];
                } else {
                    [[NSOperationQueue mainQueue] addOperationWithBlock:^{
                        [ProgressHUD showError:error];
                    }];
                }
        }];
    }
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
    AuctionDetailController* controller = (AuctionDetailController*) [segue destinationViewController];
    controller.auction = (Auction*) sender;
}

@end
