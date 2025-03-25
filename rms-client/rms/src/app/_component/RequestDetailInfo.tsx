"use client"

import style from "./requestDetailInfo.module.css";
import globalStyle from '@/css/modal.module.css';
import scrollbar from "@/css/scrollBar.module.css";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import SelectBox from "@/app/_component/SelectBox"
import React, {useCallback, useEffect, useState} from "react";
import {Request} from "@/model/Request"
import InputBox from "@/app/_component/InputBox";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {getOrganization} from "@/app/(afterLogin)/request/cart/_api/getOrganization";
import {Organization} from "@/model/Organization";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import GreenButton from "@/app/_component/GreenButton";
import {updateRequest} from "@/app/(afterLogin)/request/cart/_api/updateRequest";
import {getSampleType} from "@/app/(afterLogin)/request/cart/_api/getSampleType";
import {SampleType} from "@/model/SampleType";
import DatePickerBox from "@/app/_component/DatePickerBox";
import TextBox from "@/app/_component/TextBox";
import classNames from "classnames";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {format} from "date-fns";
import {useRequestStore} from "@/store/useRequestStore";
import {useSetProbandRequest} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {Query} from "@/model/Query";
import {getDateFromComponents, getStringDateFromComponents} from "@/app/_component/DateUtil";
import RequestDetailInfoExtension from "@/app/_component/RequestDetailInfoExtension";
import {searchRequests} from "@/app/(afterLogin)/_api/searchRequests";

type Props = {
    module?: string;
    disabled: boolean;
    serviceId: string;
    sampleId: string;
    requestGroupId: string;
    userId: string;
    closeModal: () => void;
}

export default function RequestDetailInfo({module='cart', disabled, serviceId, sampleId, requestGroupId, userId, closeModal}: Props) {
    const { request, setRequest, resetRequest } = useRequestStore();
    const setProbandRequest = useSetProbandRequest()
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<SelectBoxOption[]>([]);
    const showAlert = CallAlertDialog();

    const sexOption: SelectBoxOption[] = [
        { value: "M", name: "M" },
        { value: "F", name: "F" }
    ];

    const fetchOrganizations = useCallback(async () => {
        const response = await getOrganization(userId!);
        if (response.ok) {
            const data = await response.json();
            setOrganizationOptions(transformOrganizationsToOptions(data as Organization[]));
        }
    },[userId]);

    const fetchSampleType = useCallback(async () => {
        const response = await getSampleType(serviceId!)
        const json = await response.json()
        setSampleTypeOptions(transformSampleTypeToOptions(json as SampleType[]))
    },[serviceId])


    const handleRequestChange = (path: string, value: any) => {
        setRequest(prevState => ({
            ...prevState,
            ...setNestedValue({ ...prevState }, path, value)
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

    const fetchRequest = useCallback(async () => {
        const query: Query = {
            filter_groups:[
                {
                    filters: [
                        {
                            table: "request_group",
                            column: "id",
                            value: requestGroupId,
                            operator: "="
                        }
                    ]
                }
            ]
        }
        const response = await searchRequests(query, module)
        const json = await response.json()
        const rootRequest = json.find((req: Request) => req.service?.id === serviceId && req.sample?.id === sampleId);
        const probandRequest: Request = json.find((req: Request) => req.request_relation?.id === 1);
        setRequest(rootRequest as Request);
        setProbandRequest(probandRequest ?? null);
    },[]);


    const transformOrganizationsToOptions = (organizations: Organization[]): SelectBoxOption[] => {
        return organizations.map(org => ({
            value: org.id,
            name: org.name
        }));
    };
    const transformSampleTypeToOptions = (sampleTypes: SampleType[]): SelectBoxOption[] => {
        return sampleTypes.map(sampleType => ({
            value: sampleType.id,
            name: sampleType.name
        }));
    };
    const handleEditClick = async () => {
        if (isAllRequiredFilled()) {
            const response = await updateRequest(request!);
            if (response.ok) showAlert("Success update");
            else showAlert("Fail update");
        } else {
            showAlert("Please fill out all required fields.");
        }
    };
    const isAllRequiredFilled = () => {
        const sample = request?.sample;
        if (!sample) return false;
        const requiredFields = [
            sample?.patient?.birth_year,
            sample?.patient?.birth_month,
            sample?.patient?.birth_day,
            sample?.patient?.organization?.id,
            sample?.patient?.name,
            sample?.patient?.serial,
            sample?.patient?.sex,
            sample?.sample_type?.id,
            sample?.sampling_on,
            sample?.quantity,
        ];
        if (requiredFields.some(field => field == null || String(field).trim() === '')) {
            return false;
        }
        return (sample.extensions || []).every(
            extension =>
                !extension.required || (extension.value != null && extension.value !== '')
        );
    };

    const setAge = (birthDate: Date, samplingDate: Date): number => {
        let age = samplingDate.getFullYear() - birthDate.getFullYear();
        const monthDifference = samplingDate.getMonth() - birthDate.getMonth()
        if (monthDifference < 0 || (monthDifference === 0 && samplingDate.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    };

    const renderInstitutionName = (organization: string) => {
        return (
            <>
                <SelectBox
                    disabled={disabled}
                    label={""}
                    value={organization}
                    options={organizationOptions}
                    onChange={(value) => {
                        handleRequestChange('sample.patient.organization.id', value.value)
                        handleRequestChange('sample.patient.organization.name', value.name)
                    }}
                    width="200px"
                />
            </>
        );
    };

    const renderGender = (sex: string) => {
        return (
            <>
                <SelectBox
                    disabled={disabled}
                    label={"Gender*"}
                    value={sex}
                    options={sexOption}
                    required={true}
                    onChange={(value) => {
                        handleRequestChange('sample.patient.sex', value.value)
                    }}
                    width="200px"
                />
            </>
        );
    };

    const renderSampleType = (sampleType: string) => {
        return (
            <>
                <SelectBox
                    disabled={disabled}
                    label={"Type*"}
                    value={sampleType}
                    options={sampleTypeOptions}
                    onChange={(value) => {
                        handleRequestChange('sample.sample_type.id', value.value)
                        handleRequestChange('sample.sample_type.name', value.name)
                    }}
                    width="200px"
                />
            </>
        );
    };

    const renderBirth = (request: Request) => {
        return (
            <>
                {disabled ? (
                    <InputBox
                        label={"Date of Birth"}
                        value={getStringDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}
                        disabled={true}
                    />
                ) : (
                    <DatePickerBox
                        label={"Date of Birth"}
                        value={getDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}
                        onChange={(date) => {
                            if (date) {
                                handleRequestChange('sample.patient.birth_year', date.getFullYear());
                                handleRequestChange('sample.patient.birth_month', date.getMonth() + 1);
                                handleRequestChange('sample.patient.birth_day', date.getDate());
                                if (request?.sample?.sampling_on) {
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
                )}
            </>
        );
    };

    const renderCollectionDate = (request: Request) => {
        return (
            <>
                {disabled ? (
                    <InputBox
                        label={"Collection Date"}
                        value={request.sample?.sampling_on}
                        disabled={true}
                    />
                ) : (
                    <DatePickerBox
                        label={"Collection Date*"}
                        value={request.sample?.sampling_on}
                        onChange={(date) => {
                            if (date) {
                                handleRequestChange('sample.sampling_on', format(date, "yyyy-MM-dd"))
                                if (request?.sample?.patient?.birth_year
                                    && request?.sample?.patient?.birth_month
                                    && request?.sample?.patient?.birth_day) {
                                    handleRequestChange('sample.age', setAge(new Date(`${request?.sample.patient.birth_year}-${request?.sample.patient.birth_month}-${request?.sample.patient.birth_day}`), date));
                                }
                            } else {
                                handleRequestChange('sample.sampling_on', null)
                                handleRequestChange('sample.age', null);
                            }

                        }}
                    />
                )}
            </>
        );
    };


    useEffect(() => {
        resetRequest();
        fetchRequest()
        fetchOrganizations()
        fetchSampleType()
    }, [fetchRequest, fetchOrganizations, fetchSampleType]);

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <div className={style.modalTitle}>
                    <h1>Details</h1>
                    <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={globalStyle.modalCloseButton}/>
                </div>
                {request ? (
                    <div className={classNames(style.modalContent, scrollbar.default)}>
                        <div className={style.content}>
                            <p className={style.title}>Institution name*</p>
                            {renderInstitutionName(request.sample?.patient?.organization?.name!)}
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Service Info.</p>
                            <InputBox
                                label={"Service"}
                                value={request.service?.name}
                                disabled={true}
                            />
                        </div>
                        <p className={style.mainName}>Patient Info.</p>
                        <div className={style.section}>
                            <InputBox
                                disabled={disabled}
                                label={"Name*"}
                                value={request.sample?.patient?.name}
                                onChange={(value) => handleRequestChange('sample.patient.name', value)}
                                required={true}
                            />
                            <InputBox
                                disabled={disabled}
                                label={"MRN*"}
                                value={request.sample?.patient?.serial}
                                onChange={(value) => handleRequestChange('sample.patient.serial', value)}
                                required={true}
                            />
                            <InputBox
                                label={"Age"}
                                value={request.sample?.age}
                                disabled={true}
                                onChange={(value) => handleRequestChange('sample.age', value)}
                            />
                            {renderBirth(request)}
                        </div>
                        <div className={style.section}>
                            {renderGender(request.sample?.patient?.sex!)}
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Specimen/.Sample Info.</p>
                            {renderSampleType(request.sample?.sample_type?.name!)}
                            {renderCollectionDate(request)}
                            <InputBox
                                disabled={disabled}
                                label={"Quantity*"}
                                value={request.sample?.quantity?.toString()}
                                required={true}
                                onChange={(value) => handleRequestChange('sample.quantity', value)}
                            />
                        </div>
                        { (request.courier_company || request.awb_number) && (
                            <div className={style.content}>
                                <p className={style.title}>Airway Info.</p>
                                <InputBox
                                    disabled={disabled}
                                    label={"Global courier"}
                                    value={request.courier_company ? request.courier_company : '-'}
                                    onChange={(value) => handleRequestChange('department', value)}
                                />
                                <InputBox
                                    disabled={disabled}
                                    label={"AirWaybill no."}
                                    value={request.awb_number ? request.awb_number : '-'}
                                    onChange={(value) => handleRequestChange('ward', value)}
                                />
                            </div>
                        )}
                        <div className={style.content}>
                            <p className={style.title}>Additional Info.</p>
                            <InputBox
                                disabled={disabled}
                                label={"Medical Department"}
                                value={request.department}
                                onChange={(value) => handleRequestChange('department', value)}
                            />
                            <InputBox
                                disabled={disabled}
                                label={"Ward"}
                                value={request.ward}
                                onChange={(value) => handleRequestChange('ward', value)}
                            />
                            <InputBox
                                disabled={disabled}
                                label={"Physician Name"}
                                value={request.physician}
                                onChange={(value) => handleRequestChange('physician', value)}
                            />
                        </div>
                        {request.sample?.extensions && (
                            <RequestDetailInfoExtension disabled={disabled}/>
                        )}
                        <div className={style.memoSection}>
                            <TextBox
                                disabled={disabled}
                                label={'Memo'}
                                value={request.memo}
                                onChange={(value) => handleRequestChange('memo', value)}
                            />
                        </div>
                        {!disabled && (
                            <div className={style.modalBottom}>
                                <GreenButton
                                    name={"Edit"}
                                    onClick={handleEditClick}
                                    disabled={!isAllRequiredFilled()}
                                />
                            </div>
                        )}
                    </div>
                ) : <Loading/>}
            </div>
        </div>
    )
}