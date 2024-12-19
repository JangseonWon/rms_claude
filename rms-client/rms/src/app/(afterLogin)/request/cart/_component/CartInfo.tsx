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

type Props = {
    serviceId: string;
    sampleId: string;
    userId: string;
    closeModal: () => void;
}

export default function CartInfo({serviceId, sampleId, userId, closeModal}: Props) {
    const [request, setRequest] = useState<Request>()
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<SelectBoxOption[]>([])

    const handleRequestChange = (path: string, value: any) => {
        setRequest(prevState => ({
            ...prevState,
            ...setNestedValue({ ...prevState }, path, value)
        }));
    };
    const setNestedValue = (object: any, nestedPath: string, newValue: any): any => {
        const [firstKey, ...remainingPathSegments] = nestedPath.split('.');
        if (remainingPathSegments.length === 0) {
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
        const response = await getRequest(serviceId!, sampleId!)
        const json = await response.json()
        setRequest(json as Request)
    },[serviceId, sampleId]);

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
        if (validateRequest()) {
            const response = await updateRequest(request!);
            if (response.ok) alert("Success update")
            else alert("Fail update")
        } else {
            alert("Please fill out all required fields.");
        }
    };
    const validateRequest = () => {
        if (!request?.sample?.patient?.name) return false;
        if (!request?.sample?.patient?.serial) return false;
        if (!request?.sample?.sample_type?.name) return false;
        return request?.sample?.quantity;

    };
    const getDateFromComponents = (year?: number, month?: number, day?: number): Date | undefined => {
        if (!year || !month || !day) return undefined;
        return new Date(year, month - 1, day);
    }

    useEffect(() => {
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
                        <div className={style.content}>
                            <p className={style.title}>Patient Info.</p>
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
                            <DatePickerBox
                                label={"Date of Birth"}
                                value={getDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}
                                onChange={(date) => {
                                    handleRequestChange('sample.patient.birth_year', date.getFullYear());
                                    handleRequestChange('sample.patient.birth_month', date.getMonth() + 1);
                                    handleRequestChange('sample.patient.birth_day', date.getDate());
                                }}
                            />
                            <InputBox
                                label={"Age"}
                                value={request.sample?.age}
                                onChange={(value) => handleRequestChange('sample.age', value)}
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
                            />
                            <DatePickerBox
                                label={"Date or collection*"}
                                value={request.sample?.sampling_on}
                                onChange={(date) => handleRequestChange('sample.sampling_on', date)}
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
                        <div className={style.memoSection}>
                            <TextBox
                                label={'Memo'}
                                value={request.memo}
                                required={true}
                                onChange={(value) => handleRequestChange('memo', value)}
                            />
                        </div>
                        <div className={style.modalBottom}>
                            <GreenButton name={"Edit"} onClick={handleEditClick}/>
                        </div>
                    </div>
                ) : <Loading/>}
            </div>
        </div>
    )
}