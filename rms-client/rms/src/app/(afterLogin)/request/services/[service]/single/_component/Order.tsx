'use client';

import style from "@/app/(afterLogin)/request/services/[service]/single/_component/order.module.css"
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import React, {useCallback, useEffect, useState} from "react";
import {Organization} from "@/model/Organization";
import {getOrganization} from "@/app/(afterLogin)/request/services/[service]/single/_api/getOrganization";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {Request} from "@/model/Request";
import DatePickerBox from "@/app/_component/DatePickerBox";
import {SampleType} from "@/model/SampleType";
import {getSampleType} from "@/app/(afterLogin)/request/services/[service]/single/_api/getSampleType"
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {putRequest} from "@/app/(afterLogin)/request/services/[service]/single/_api/putRequest";
import {format} from "date-fns";
import {usePathname} from "next/navigation";
import ExtensionInputComponent
    from "@/app/(afterLogin)/request/services/[service]/single/_component/ExtensionInputComponent";
import TextBox from "@/app/_component/TextBox";

export default function Order() {
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<SelectBoxOption[]>([])
    const [request, setRequest] = useState<Request>({})
    const [selectedOrganization, setSelectedOrganization] = useState<SelectBoxOption | null>(null);
    const [selectedSampleType, setSelectedSampleType] = useState<SelectBoxOption | null>(null);
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);

    const fetchOrganizations = useCallback(async () => {
        const response = await getOrganization()
        const data = await response.json();
        setOrganizationOptions(transformOrganizationToOptions(data as Organization[]))
    },[]);


    const fetchSampleType = async (serviceId: string) => {
        const response = await getSampleType(serviceId)
        const data = await response.json();
        setSampleTypeOptions(transformSampleTypeToOptions(data as SampleType[]))
    }

    useEffect(() => {
        fetchOrganizations();
        fetchSampleType(serviceId);
    }, [fetchOrganizations]);

    const publishRequest = (status:string) => {
        const updateRequest = [{
            ...request, ...{status: status}
        }]
        putRequest(updateRequest)
            .then((res) =>{
                if(res.ok) {
                    alert("success!")
                }
                else alert("fail")
            })
    };

    const handleRequestChange = (path: string, value: any):void => {
        setRequest(prevState => ({
            ...prevState,
            ...setNestedValue({...prevState}, path, value)
        }));
    };

    const setNestedValue = (object: any, nestedPath: string, newValue: any): any => {
        const [firstKey, ...remainingPathSegments] = nestedPath.split('.');
        if (remainingPathSegments.length === 0) {
            if (newValue === null) {
                const { [firstKey]: removed, ...rest } = object;
                return rest;
            }
            return { ...object, [firstKey]: newValue };
        }
        if (!object[firstKey]) {
            object[firstKey] = {};
        }
        return {
            ...object,
            [firstKey]: setNestedValue(object[firstKey] || {}, remainingPathSegments.join('.'), newValue),
        };
    };

    const transformOrganizationToOptions = (data: Organization[]): SelectBoxOption[] => {
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };

    const transformSampleTypeToOptions = (data: SampleType[]): SelectBoxOption[] => {
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };

    const setAge = (birthDate: Date, samplingDate: Date): number => {
        let age = samplingDate.getFullYear() - birthDate.getFullYear();
        const monthDifference = samplingDate.getMonth() - birthDate.getMonth()
        if (monthDifference < 0 || (monthDifference === 0 && samplingDate.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    };

    const isAllRequiredFilled = () => {
        if (!request?.sample?.patient?.name) return false;
        if (!request?.sample?.patient?.serial) return false;
        if (!request?.sample?.sample_type?.id) return false;
        if (!request?.sample?.sampling_on) return false;
        return request?.sample?.quantity;

    };

    return (
        <div className={style.container}>
            <p className={style.mainName}>Institution name *</p>
            <div className={style.section}>
                <div className={style.selectBox}>
                    <SelectBox
                        label={""}
                        options={organizationOptions}
                        value={selectedOrganization}
                        required={true}
                        onChange={(value) => {
                            handleRequestChange('sample.patient.organization.id', value.value)
                            handleRequestChange('sample.patient.organization.name', value.name)
                            setSelectedOrganization(value.name);
                        }}
                        width="11vw"
                    />
                </div>
            </div>
            <p className={style.mainName}>Patient Info.</p>
            <div className={style.section}>
                <InputBox
                    label={"Name*"}
                    required={true}
                    onChange={(value) => handleRequestChange('sample.patient.name', value)}
                />
                <InputBox
                    label={"MRN*"}
                    required={true}
                    onChange={(value) => handleRequestChange('sample.patient.serial', value)}
                />
                <div className={style.dateBox}>
                    <DatePickerBox
                        label={"Date of Birth"}
                        onChange={(date) => {
                            if (date) {
                                handleRequestChange('sample.patient.birth_year', date.getFullYear());
                                handleRequestChange('sample.patient.birth_month', date.getMonth() + 1);
                                handleRequestChange('sample.patient.birth_day', date.getDate());
                                if (request.sample?.sampling_on) {
                                    handleRequestChange('sample.age', setAge(date, new Date(request.sample.sampling_on)));
                                }
                            } else {
                                handleRequestChange('sample.patient.birth_year', null);
                                handleRequestChange('sample.patient.birth_month', null);
                                handleRequestChange('sample.patient.birth_day', null);
                                handleRequestChange('sample.age', null);
                            }
                        }}
                    />
                </div>
                <InputBox
                    label={"Age"}
                    disabled={true}
                    value={request.sample?.age}
                />
            </div>
            <p className={style.mainName}>Specimen/ .Sample Info.</p>
            <div className={style.section}>
                <div className={style.selectBox}>
                    <SelectBox
                        label={"Type*"}
                        value={selectedSampleType}
                        options={sampleTypeOptions}
                        required={true}
                        onChange={(value) => {
                            handleRequestChange('sample.sample_type.id', value.value)
                            handleRequestChange('sample.sample_type.name', value.name)
                            setSelectedSampleType(value.name);
                        }}
                        width="11vw"
                    />
                </div>
                <div className={style.dateBox}>
                    <DatePickerBox
                        label={"Date of Collection*"}
                        required={true}
                        onChange={(date) => {
                            if (date) {
                                handleRequestChange('sample.sampling_on', format(date, "yyyy-MM-dd"))
                                if (request.sample?.patient?.birth_year
                                    && request.sample?.patient?.birth_month
                                    && request.sample?.patient?.birth_day) {
                                    handleRequestChange('sample.age', setAge(new Date(`${request.sample.patient.birth_year}-${request.sample.patient.birth_month}-${request.sample.patient.birth_day}`), date));
                                }
                            } else {
                                handleRequestChange('sample.sampling_on', null)
                                handleRequestChange('sample.age', null);
                            }

                        }}
                    />
                </div>
                <InputBox
                    label={"Quantity*"}
                    required={true}
                    onChange={(value) => handleRequestChange('sample.quantity', value)}
                />
            </div>
            <p className={style.mainName}>Additional Info.</p>
            <div className={style.section}>
                <InputBox
                    label={"Medical Department"}
                    onChange={(value) => handleRequestChange('department', value)}
                />
                <InputBox
                    label={"Ward"}
                    onChange={(value) => handleRequestChange('ward', value)}
                />
                <InputBox
                    label={"Physician Name"}
                    onChange={(value) => handleRequestChange('physician', value)}
                />
            </div>
            <ExtensionInputComponent/>
            <div className={style.memoSection}>
                <TextBox
                    label={'Memo'}
                    value={request.memo}
                    required={true}
                    onChange={(value) => handleRequestChange('memo', value)}
                />
            </div>
            <div className={style.buttonSection}>
                <GreenButton
                    name={"Add to Cart"}
                    disabled={!isAllRequiredFilled()}
                    onClick={() => publishRequest("CART")}
                />
                <BlueButton
                    name={"Order Now"}
                    disabled={!isAllRequiredFilled()}
                    onClick={() => publishRequest("ORDERED")}
                />
            </div>
        </div>
    )
}