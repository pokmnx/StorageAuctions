//
//  ServiceManager.m
//  StorageAuctions
//
//  Created by PSIHPOK on 6/18/18.
//  Copyright © 2018 PSIHPOK. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ServiceManager.h"
#import "AFNetworking.h"
@import GoogleMaps;


NSString* baseURL = @"https://dev3.storageauctions.net/block";

@interface ServiceManager ()

@end

@implementation ServiceManager

+(ServiceManager*) sharedManager {
    static ServiceManager *sharedMyManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedMyManager = [[self alloc] init];
    });
    return sharedMyManager;
}

-(void) loginWith:(NSString*) username password:(NSString*) password completion:(void (^)(BOOL bSuccess, NSString* error)) completion {
    NSData* body = [self getDataFrom:[NSString stringWithFormat:@"email=%@&password=%@", username, password]];
    NSString* url = [baseURL stringByAppendingString:@"/user/login"];
    [self postRequest:url body:body completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        self.bLoginFirstTime = false;
        if (!error) {
            NSLog(@"Reply JSON: %@", responseObject);
            if ([responseObject isKindOfClass:[NSDictionary class]]) {
                self.bLoginFirstTime = false;
                completion(true, NULL);
            }
        } else {
            NSLog(@"Login Error: %@, %@, %@", error, response, responseObject);
            NSDictionary* errDic = (NSDictionary*)[((NSDictionary*)responseObject) objectForKey:@"errors"];
            NSString* emailErrStr = [errDic objectForKey:@"email"];
            if (emailErrStr.length > 0){
                completion(false, emailErrStr);
            } else {
                NSString* passwordErrStr = [errDic objectForKey:@"password"];
                completion(false, passwordErrStr);
            }
        }
    }];
}

-(void) signupWith:(NSString*) email password:(NSString*) password username:(NSString*) username phone:(NSString*) phone name:(NSString*) name role:(NSString*) role completion:(void (^)(BOOL bSuccess, NSString* error)) completion {
    NSData* body = [self getDataFrom:[NSString stringWithFormat:@"email=%@&username=%@&password=%@&phone=%@&name=%@&role=%@", email, username, password, phone, name, role]];
    NSString* url = [baseURL stringByAppendingString:@"/user/set"];
    [self postRequest:url body:body completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        self.bLoginFirstTime = false;
        if (!error) {
            NSLog(@"Reply JSON: %@", responseObject);
            if ([responseObject isKindOfClass:[NSDictionary class]]) {
                self.bLoginFirstTime = true;
                completion(true, NULL);
            }
        } else {
            NSLog(@"Login Error: %@, %@, %@", error, response, responseObject);
            NSDictionary* errDic = (NSDictionary*)[((NSDictionary*)responseObject) objectForKey:@"errors"];
            NSString* emailErrStr = [errDic objectForKey:@"email"];
            NSString* passErrStr = [errDic objectForKey:@"password"];
            NSString* userErrStr = [errDic objectForKey:@"username"];
            if (emailErrStr.length > 0){
                completion(false, emailErrStr);
            } else if (passErrStr.length > 0) {
                completion(false, passErrStr);
            } else if (userErrStr.length > 0) {
                completion(false, userErrStr);
            }
        }
    }];
}

- (void) addNewFacilityWith:(NSString*) name email:(NSString*) email website:(NSString*) website phone:(NSString*) phone taxRate:(CGFloat) taxRate commissionRate:(CGFloat) commissionRate terms:(NSString*) terms address:(LMAddress*) address completion:(void (^)(BOOL bSuccess, NSString* error)) completion {
    
    NSArray* addressArr = [address.formattedAddress componentsSeparatedByString:@","];
    NSString* state_code = @"";
    if (addressArr.count >= 2) {
        NSArray* stateArr = [addressArr[addressArr.count - 2] componentsSeparatedByString:@" "];
        state_code = stateArr[stateArr.count - 2];
    }
    
    NSString* requestStr = [NSString stringWithFormat:@"name=%@&website=%@&email=%@&commission_rate=%f&taxrate=%f&terms=%@&lat=%f&lon=%f&acc=%d&phone=%@&address=%@&city=%@&postal_code=%@&state_code=%@", name, website, email, commissionRate, taxRate, terms, address.coordinate.latitude, address.coordinate.longitude, 5, phone, [address.route stringByAppendingString:address.streetNumber] , address.locality, address.postalCode, state_code];
    NSData* body = [self getDataFrom:requestStr];
    NSString* url = [baseURL stringByAppendingString:@"/facility/set"];
    [self postRequest:url body:body completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        if (!error) {
            NSLog(@"Reply JSON: %@", responseObject);
            if ([responseObject isKindOfClass:[NSDictionary class]]) {
                completion(true, NULL);
            }
        } else {
            NSLog(@"Add Facility Error: %@, %@, %@", error, response, responseObject);
            NSDictionary* errDic = (NSDictionary*)[((NSDictionary*)responseObject) objectForKey:@"errors"];
            NSString* nameErrStr = [errDic objectForKey:@"name"];
            NSString* phoneErrStr = [errDic objectForKey:@"phone"];
            NSString* stateErrStr = [errDic objectForKey:@"state_code"];
            NSString* emailErrStr = [errDic objectForKey:@"email"];
            
            if (nameErrStr.length > 0){
                completion(false, nameErrStr);
            } else if (phoneErrStr.length > 0) {
                completion(false, phoneErrStr);
            } else if (stateErrStr.length > 0) {
                completion(false, stateErrStr);
            } else if (emailErrStr.length > 0) {
                completion(false, emailErrStr);
            }
        }
    }];
}

- (void) getAllFacilities:(void (^)(NSArray* facArr)) completion {
    NSString* url = [baseURL stringByAppendingString:@"/user/facility/get"];
    [self getRequest:url body:NULL completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        if (!error) {
            NSLog(@"Reply JSON: %@", responseObject);
            if ([responseObject isKindOfClass:[NSDictionary class]]) {
                NSArray* dataArr = [(NSDictionary*)responseObject objectForKey:@"facilities"];
                NSMutableArray* fArr = [NSMutableArray array];
                for (NSInteger index = 0; index < dataArr.count; index++) {
                    NSDictionary* dic = [dataArr objectAtIndex:index];
                    Facility* facility = [[Facility alloc] init];
                    facility.fac_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"id"]];
                    facility.user_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"user_id"]];
                    facility.active = [self getIntFrom:(NSNumber*)[dic objectForKey:@"active"]];
                    facility.name = (NSString*)[dic objectForKey:@"name"];
                    facility.address = (NSString*)[dic objectForKey:@"address"];
                    facility.city = (NSString*)[dic objectForKey:@"city"];
                    facility.acc = [self getIntFrom:(NSNumber*)[dic objectForKey:@"acc"]];
                    facility.state_code = (NSString*)[dic objectForKey:@"state_code"];
                    facility.postal_code = (NSString*)[dic objectForKey:@"postal_code"];
                    facility.country = (NSString*)[dic objectForKey:@"country"];
                    facility.lat = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"lat"]];
                    facility.lon = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"lon"]];
                    facility.phone = (NSString*)[dic objectForKey:@"phone"];
                    facility.website = (NSString*)[dic objectForKey:@"website"];
                    facility.email = (NSString*)[dic objectForKey:@"email"];
                    facility.time_created = [self getDateFrom:(NSString*)[dic objectForKey:@"time_created"]];
                    facility.time_updated = [self getDateFrom:(NSString*)[dic objectForKey:@"time_updated"]];
                    facility.source = (NSString*)[dic objectForKey:@"source"];
                    facility.source_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"source_id"]];
                    facility.terms = (NSString*)[dic objectForKey:@"terms"];
                    facility.taxrate = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"taxrate"]];
                    facility.remote_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"remote_id"]];
                    facility.commission_rate = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"commission_rate"]];
                    facility.url = (NSString*)[dic objectForKey:@"url"];
                    [fArr addObject:facility];
                }
                completion(fArr);
            } else {
                completion(NULL);
            }
        } else {
            NSLog(@"Get All Facility Error: %@, %@, %@", error, response, responseObject);
            // NSDictionary* errDic = (NSDictionary*)[((NSDictionary*)responseObject) objectForKey:@"errors"];
            completion(NULL);
        }
    }];
}

- (void) createAuction:(AuctionRequest*) request completion:(void (^)(BOOL bSuccess, NSString* error)) completion {
    NSString* url = [baseURL stringByAppendingString:@"/auction/add"];
    NSString* requestStr = [NSString stringWithFormat:@"amount_owed=%@&alt_unit_number=%@&reserve=%@&time_end=%@&terms=%@&prebid_close=%@&lock_tag=%@&fees=%@&batch_email=%@&cleanout_other=%@&payment=%@&time_st_zone=%@&cleanout=%@&access=%@&auction_type=%@&time_start=%@&facility_id=%@&unit_size=%@&descr=%@&unit_number=%@&time_end_zone=%@&save_terms=%@&payment_other=%@&tenant_name=%@", request.amount_owed, request.alt_unit_number, request.reserve, request.time_end, request.terms, request.prebid_close, request.lock_tag, request.fees, request.batch_email, request.cleanout_other, request.payment, request.time_st_zone, request.cleanout, request.access, request.auction_type, request.time_start, request.facility_id, request.unit_size, request.descr, request.unit_number, request.time_end_zone, request.save_terms, request.payment_other, request.tenant_name];
    NSData* body = [self getDataFrom:requestStr];
    [self postRequest:url body:body completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        if (!error) {
            NSLog(@"Reply JSON: %@", responseObject);
            if ([responseObject isKindOfClass:[NSDictionary class]]) {
                completion(true, NULL);
            }
        } else {
            NSLog(@"Add Facility Error: %@, %@, %@", error, response, responseObject);
            NSDictionary* errDic = (NSDictionary*)[((NSDictionary*)responseObject) objectForKey:@"errors"];
            NSString* unitNumErrStr = [errDic objectForKey:@"unit_number"];
            NSString* altUnitNumErrStr = [errDic objectForKey:@"alt_unit_number"];
            NSString* timeEndErrStr = [errDic objectForKey:@"time_end"];
            NSString* facilityIDErrStr = [errDic objectForKey:@"facility_id"];
            NSString* unitSizeErrStr = [errDic objectForKey:@"unit_size"];
            NSString* cleanOutErrStr = [errDic objectForKey:@"cleanout"];
            NSString* accessErrStr = [errDic objectForKey:@"access"];
            
            if (unitNumErrStr.length > 0) {
                completion(false, unitNumErrStr);
            } else if (altUnitNumErrStr.length > 0) {
                completion(false, altUnitNumErrStr);
            } else if (timeEndErrStr.length > 0) {
                completion(false, timeEndErrStr);
            } else if (facilityIDErrStr.length > 0) {
                completion(false, facilityIDErrStr);
            } else if (unitSizeErrStr.length > 0) {
                completion(false, unitSizeErrStr);
            } else if (cleanOutErrStr.length > 0) {
                completion(false, cleanOutErrStr);
            } else if (accessErrStr.length > 0) {
                completion(false, accessErrStr);
            }
        }
    }];
}

- (void) getAllAuctions:(void (^)(NSArray* auctArr)) completion {
    NSString* url = [baseURL stringByAppendingString:@"/user/selling"];
    [self getRequest:url body:NULL completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        if (!error) {
            NSLog(@"Reply JSON: %@", responseObject);
            if ([responseObject isKindOfClass:[NSDictionary class]]) {
                NSArray* dataArr = [(NSDictionary*)responseObject objectForKey:@"auctions"];
                NSMutableArray* aArr = [NSMutableArray array];
                for (NSInteger index = 0; index < dataArr.count; index++) {
                    NSDictionary* dic = [dataArr objectAtIndex:index];
                    Auction* auction = [[Auction alloc] init];
                    
                    auction.auct_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"id"]];
                    auction.facility_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"facility_id"]];
                    auction.auction_online_bid_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"auction_online_bid_id"]];
                    auction.status = [self getIntFrom:(NSNumber*)[dic objectForKey:@"status"]];
                    auction.failed_reason = (NSString*)[dic objectForKey:@"failed_reason"];
                    auction.descr = (NSString*)[dic objectForKey:@"descr"];
                    auction.time_start = [self getDateFrom:(NSString*)[dic objectForKey:@"time_start"]];
                    auction.time_st_zone = (NSString*)[dic objectForKey:@"time_st_zone"];
                    auction.time_end = [self getDateFrom:(NSString*)[dic objectForKey:@"time_end"]];
                    auction.time_end_zone = (NSString*)[dic objectForKey:@"time_end_zone"];
                    auction.amount_owed = (NSString*)[dic objectForKey:@"amount_owed"];
                    auction.fees = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"fees"]];
                    auction.tenant_name = (NSString*)[dic objectForKey:@"tenant_name"];
                    auction.unit_number = [self getIntFrom:(NSNumber*)[dic objectForKey:@"unit_number"]];
                    auction.unit_size = [self getIntFrom:(NSNumber*)[dic objectForKey:@"unit_size"]];
                    auction.lock_tag = (NSString*)[dic objectForKey:@"lock_tag"];
                    auction.time_created = [self getDateFrom:(NSString*)[dic objectForKey:@"time_created"]];
                    auction.time_updated = [self getDateFrom:(NSString*)[dic objectForKey:@"time_updated"]];
                    auction.bid_count = [self getIntFrom:(NSNumber*)[dic objectForKey:@"bid_count"]];
                    auction.winner_code = [self getIntFrom:(NSNumber*)[dic objectForKey:@"winner_code"]];
                    auction.seller_fee = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"seller_fee"]];
                    auction.buyer_fee = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"buyer_fee"]];
                    auction.emailed = [self getIntFrom:(NSNumber*)[dic objectForKey:@"emailed"]];
                    auction.terms = (NSString*)[dic objectForKey:@"terms"];
                    auction.queue_flag = [self getIntFrom:(NSNumber*)[dic objectForKey:@"queue_flag"]];
                    auction.auction_id = (NSString*)[dic objectForKey:@"auction_id"];
                    auction.meta = [[AuctionMeta alloc] init];
                    NSDictionary* metaDic = [dic objectForKey:@"meta"];
                    if (metaDic != [NSNull null] && metaDic.count > 0) {
                        auction.meta.cleanout = [self getIntFrom:(NSNumber*)[metaDic objectForKey:@"cleanout"]];
                        auction.meta.cleanout_other = (NSString*)[metaDic objectForKey:@"cleanout_other"];
                        auction.meta.payment = [self getIntFrom:(NSNumber*)[metaDic objectForKey:@"payment"]];
                        auction.meta.payment_other = (NSString*)[metaDic objectForKey:@"payment_other"];
                        auction.meta.access = [self getIntFrom:(NSNumber*)[metaDic objectForKey:@"access"]];
                        auction.meta.currentBidPrice = [self getFloatFrom:(NSNumber*)[metaDic objectForKey:@"ibid_current_price"]];
                        NSDictionary* imageDic = [metaDic objectForKey:@"ibid_images"];
                        if (imageDic.count > 0) {
                            auction.meta.images = [NSArray arrayWithArray:imageDic.allKeys];
                        }
                    } else {
                        auction.meta = NULL;
                    }
                    
                    auction.tax = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"tax"]];
                    auction.remote_id = (NSString*)[dic objectForKey:@"remote_id"];
                    auction.source_id = (NSString*)[dic objectForKey:@"source_id"];
                    auction.reserve = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"reserve"]];
                    auction.bid_schedule_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"bid_schedule_id"]];
                    auction.alt_unit_number = [self getIntFrom:(NSNumber*)[dic objectForKey:@"alt_unit_number"]];
                    auction.batch_email = [self getIntFrom:(NSNumber*)[dic objectForKey:@"batch_email"]];
                    auction.title = (NSString*)[dic objectForKey:@"title"];
                    auction.facility_name = (NSString*)[dic objectForKey:@"facility_name"];
                    auction.address = (NSString*)[dic objectForKey:@"address"];
                    auction.city = (NSString*)[dic objectForKey:@"city"];
                    auction.state_code = (NSString*)[dic objectForKey:@"state_code"];
                    auction.user_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"user_id"]];
                    auction.amount = [self getFloatFrom:(NSNumber*)[dic objectForKey:@"amount"]];
                    auction.bid_user_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"bid_user_id"]];
                    auction.media_auction_id = [self getIntFrom:(NSNumber*)[dic objectForKey:@"media_auction_id"]];
                    auction.photo_path_size0 = (NSString*)[dic objectForKey:@"photo_path_size0"];
                    auction.photo_path_size1 = (NSString*)[dic objectForKey:@"photo_path_size1"];
                    auction.photo_path_size2 = (NSString*)[dic objectForKey:@"photo_path_size2"];
                    auction.photo_path_size3 = (NSString*)[dic objectForKey:@"photo_path_size3"];
                    auction.view_count = [self getIntFrom:(NSNumber*)[dic objectForKey:@"view_count"]];
                    auction.os_date = (NSString*)[dic objectForKey:@"os_date"];
                    auction.os_time = (NSString*)[dic objectForKey:@"os_time"];
                    auction.url = (NSString*)[dic objectForKey:@"url"];
                    auction.group = (NSString*)[dic objectForKey:@"group"];
                    auction.type = (NSString*)[dic objectForKey:@"type"];
                    auction.category = (NSString*)[dic objectForKey:@"category"];
                    
                    [aArr addObject:auction];
                }
                completion(aArr);
            } else {
                completion(NULL);
            }
        } else {
            completion(NULL);
        }
    }];
}

-(void) getRequest:(NSString*) url body:(NSData*) body completionHandler:(void (^)(NSURLResponse *response, id responseObject, NSError *error))completionHandler {
    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    NSMutableURLRequest *req = [[AFJSONRequestSerializer serializer] requestWithMethod:@"GET" URLString:url parameters:nil error:nil];
    
    req.timeoutInterval = [[[NSUserDefaults standardUserDefaults] valueForKey:@"timeoutInterval"] longValue];
    [req setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [req setValue:@"Basic ZGV2OnNhc2E=" forHTTPHeaderField:@"Authorization"];
    if (body != NULL)
        [req setHTTPBody:body];
    
    [[manager dataTaskWithRequest:req completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        completionHandler(response, responseObject, error);
    }] resume];
}

-(void) postRequest:(NSString*) url body:(NSData*) body completionHandler:(void (^)(NSURLResponse *response, id responseObject, NSError *error))completionHandler {
    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    NSMutableURLRequest *req = [[AFJSONRequestSerializer serializer] requestWithMethod:@"POST" URLString:url parameters:nil error:nil];
    
    req.timeoutInterval = [[[NSUserDefaults standardUserDefaults] valueForKey:@"timeoutInterval"] longValue];
    [req setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [req setValue:@"Basic ZGV2OnNhc2E=" forHTTPHeaderField:@"Authorization"];
    [req setHTTPBody:body];
    
    [[manager dataTaskWithRequest:req completionHandler:^(NSURLResponse * _Nonnull response, id  _Nullable responseObject, NSError * _Nullable error) {
        completionHandler(response, responseObject, error);
    }] resume];
/*
    NSMutableURLRequest *urlRequest = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:url]];
    
    //create the Method "GET" or "POST"
    [urlRequest setHTTPMethod:@"POST"];
    [urlRequest setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [urlRequest setValue:@"Basic ZGV2OnNhc2E=" forHTTPHeaderField:@"Authorization"];
    [urlRequest setHTTPBody:body];
    
    
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:urlRequest completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if(httpResponse.statusCode == 200)
        {
            NSError *parseError = nil;
            NSDictionary *responseObject = [NSJSONSerialization JSONObjectWithData:data options:0 error:&parseError];
            completionHandler(response, responseObject, error);
        }
        else
        {
            completionHandler(nil, nil, error);
        }
    }];
    [dataTask resume];
*/
}

-(NSData*) getDataFrom:(NSString*) str {
    NSData* data = [str dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:true];
    return data;
}

- (NSDate*) getDateFrom:(NSString*) str {
    if (str == NULL)
        return NULL;
    else if (str == [NSNull null])
        return NULL;
    NSDateFormatter* dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd hh:mm:ss"];
    return [dateFormatter dateFromString:str];
}

- (CGFloat) getFloatFrom:(NSNumber*) object {
    if (object == NULL)
        return 0;
    else if (object == [NSNull null])
        return 0;
    
    return [object floatValue];
}

- (NSInteger) getIntFrom:(NSNumber*) object {
    if (object == NULL)
        return 0;
    else if (object == [NSNull null])
        return 0;
    return [object integerValue];
}

@end
