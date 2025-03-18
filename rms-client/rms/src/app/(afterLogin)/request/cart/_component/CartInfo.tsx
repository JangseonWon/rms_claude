"use client"

import style from "@/app/(afterLogin)/request/cart/_component/cartInfo.module.css";
import globalStyle from '@/css/modal.module.css';
import scrollbar from "@/css/scrollBar.module.css";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import SelectBox from "@/app/_component/SelectBox"
import React, {useCallback, useEffect, useState} from "react";
import {getRequest} from "@/app/(afterLogin)/request/cart/_api/getRequest";
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
import CartInfoExtensionComponent from "@/app/(afterLogin)/request/cart/_component/CartInfoExtensionComponent";
import {Extension, ExtensionType} from "@/model/Extension";
import {format} from "date-fns";
import {useRequestStore} from "@/store/useRequestStore";
import {useProbandRequest, useSetProbandRequest} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {Query} from "@/model/Query";
import {searchRequests} from "@/app/(afterLogin)/request/cart/_api/searchRequests";

type Props = {
    serviceId: string;
    sampleId: string;
    requestGroupId: string;
    userId: string;
    closeModal: () => void;
}

export default function CartInfo({serviceId, sampleId, requestGroupId, userId, closeModal}: Props) {
    const [ requests, setRequests ] = useState<Request[]>([])
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
    /*const fetchRequest = useCallback(async () => {
        const response = await getRequest(serviceId!, sampleId!)
        const json = await response.json()
        setRequest(json as Request)
    },[serviceId, sampleId]);*/
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
        const response = await searchRequests(query)
        const json = await response.json()
        const rootRequest = json.find((req: Request) => req.service?.id === serviceId && req.sample?.id === sampleId);
        const probandRequest: Request = json.find((req: Request) => req.request_relation?.id === 1);
        console.log(JSON.stringify(probandRequest, null, 2))
        setRequests(json as Request[]);
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

    const getDateFromComponents = (year?: number, month?: number, day?: number): Date | undefined => {
        if (!year || !month || !day) return undefined;
        return new Date(year, month - 1, day);
    }

    const setAge = (birthDate: Date, samplingDate: Date): number => {
        let age = samplingDate.getFullYear() - birthDate.getFullYear();
        const monthDifference = samplingDate.getMonth() - birthDate.getMonth()
        if (monthDifference < 0 || (monthDifference === 0 && samplingDate.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
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
                    <h1>Cart Details</h1>
                    <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={globalStyle.modalCloseButton}/>
                </div>
                {request ? (
                    <div className={classNames(style.modalContent, scrollbar.default)}>
                        <div className={style.content}>
                            <p className={style.title}>Institution name*</p>
                            <SelectBox
                                label={""}
                                value={request.sample?.patient?.organization?.name}
                                options={organizationOptions}
                                onChange={(value) => {
                                    handleRequestChange('sample.patient.organization.id', value.value)
                                    handleRequestChange('sample.patient.organization.name', value.name)
                                }}
                                width="200px"
                            />
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
                                label={"Name*"}
                                value={request.sample?.patient?.name}
                                onChange={(value) => handleRequestChange('sample.patient.name', value)}
                                required={true}
                            />
                            <InputBox
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
                        </div>
                        <div className={style.section}>
                            <SelectBox
                                label={"Gender*"}
                                value={request.sample?.patient?.sex}
                                options={sexOption}
                                required={true}
                                onChange={(value) => {
                                    handleRequestChange('sample.patient.sex', value.value)
                                }}
                                width="200px"
                            />
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Specimen/.Sample Info.</p>
                            <SelectBox
                                label={"Type*"}
                                value={request.sample?.sample_type?.name}
                                options={sampleTypeOptions}
                                onChange={(value) => {
                                    handleRequestChange('sample.sample_type.id', value.value)
                                    handleRequestChange('sample.sample_type.name', value.name)
                                }}
                                width="200px"
                            />
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
                            <InputBox
                                label={"Quantity*"}
                                value={request.sample?.quantity?.toString()}
                                required={true}
                                onChange={(value) => handleRequestChange('sample.quantity', value)}
                            />
                        </div>
                        <div className={style.content}>
                            <p className={style.title}>Additional Info.</p>
                            <InputBox
                                label={"Medical Department"}
                                value={request.department}
                                onChange={(value) => handleRequestChange('department', value)}
                            />
                            <InputBox
                                label={"Ward"}
                                value={request.ward}
                                onChange={(value) => handleRequestChange('ward', value)}
                            />
                            <InputBox
                                label={"Physician Name"}
                                value={request.physician}
                                onChange={(value) => handleRequestChange('physician', value)}
                            />
                        </div>
                        {request.sample?.extensions && (
                            <CartInfoExtensionComponent extensions={request.sample.extensions}/>
                        )}
                        <div className={style.memoSection}>
                            <TextBox
                                label={'Memo'}
                                value={request.memo}
                                onChange={(value) => handleRequestChange('memo', value)}
                            />
                        </div>
                        <div className={style.modalBottom}>
                            <GreenButton
                                name={"Edit"}
                                onClick={handleEditClick}
                                disabled={!isAllRequiredFilled()}
                            />
                        </div>
                    </div>
                ) : <Loading/>}
            </div>
        </div>
    )
}