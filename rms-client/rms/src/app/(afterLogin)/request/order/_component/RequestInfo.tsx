"use client"

import style from "@/app/(afterLogin)/request/order/_component/requestInfo.module.css";
import globalStyle from '@/css/modal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useCallback, useEffect, useState} from "react";
import {Request} from "@/model/Request"
import InputBox from "@/app/_component/InputBox";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {getRequestOrderInfo} from "@/app/(afterLogin)/request/order/_api/getRequestOrderInfo";
import classNames from "classnames";
import scroll from "@/css/scrollBar.module.css";
import RequestInfoExtensionComponent from "@/app/(afterLogin)/request/order/_component/RequestInfoExtensionComponent";

type Props = {
    serviceId: string;
    sampleId: string;
    closeModal: () => void;
}

export default function RequestInfo({serviceId, sampleId, closeModal}: Props) {
    const [request, setRequest] = useState<Request>({});

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
        const response = await getRequestOrderInfo(serviceId!, sampleId!)
        const json = await response.json()
        setRequest(json as Request)
    },[serviceId, sampleId]);

    const formatDate = (date: Date): string => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    const getDateFromComponents = (year?: number, month?: number, day?: number): string | undefined => {
        if (!year || !month || !day) return undefined;
        const date = new Date(year, month - 1, day);
        return formatDate(date);
    };

    useEffect(() => {
        fetchRequest()
    }, []);

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <div className={style.modalTitle}>
                    <h1>Order Details</h1>
                    <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={globalStyle.modalCloseButton}/>
                </div>
                {request ? (
                    <div className={classNames(style.wrapper, scroll.default)}>
                        <div className={style.modalContent}>
                            <div className={style.headerContent}>
                                <div className={style.firstContent}>
                                    <p className={style.firstTitle}>Institution name</p>
                                    <InputBox
                                        label={"Institution"}
                                        value={request.sample?.patient?.organization?.name}
                                        disabled={true}
                                    />
                                </div>
                                <div className={style.firstContent}>
                                    <p className={style.firstTitle}>Service Info.</p>
                                    <InputBox
                                        label={"Service"}
                                        value={request.service?.name}
                                        disabled={true}
                                    />
                                </div>
                            </div>
                            <p className={style.title}>Patient Info.</p>
                            <div className={style.section}>
                                <InputBox
                                    label={"Name*"}
                                    value={request.sample?.patient?.name}
                                    onChange={(value) => handleRequestChange('sample.patient.name', value)}
                                    disabled={true}
                                />
                                <InputBox
                                    label={"MRN*"}
                                    value={request.sample?.patient?.serial}
                                    onChange={(value) => handleRequestChange('sample.patient.serial', value)}
                                    disabled={true}
                                />
                                <InputBox
                                    label={"Age"}
                                    value={request.sample?.age}
                                    disabled={true}
                                />
                                <InputBox
                                    label={"Date of Birth"}
                                    value={getDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}
                                    disabled={true}
                                />
                            </div>
                            <div className={style.section}>
                                <InputBox
                                    label={"Gender"}
                                    value={request.sample?.patient?.sex === "M" ? "Male" : "Female"}
                                    disabled={true}
                                />
                            </div>
                            <div className={style.content}>
                                <p className={style.title}>Additional Info.</p>
                                <InputBox
                                    label={"Medical Department"}
                                    value={request.department}
                                    disabled={true}
                                />
                                <InputBox
                                    label={"Physician Name"}
                                    value={request.physician}
                                    disabled={true}
                                />
                            </div>
                            <div className={style.content}>
                                <p className={style.title}>Specimen/.Sample Info.</p>
                                <InputBox
                                    label={"Type*"}
                                    value={request.sample?.sample_type?.name}
                                    disabled={true}
                                />
                                <InputBox
                                    label={"Collection Date*"}
                                    value={request.sample?.sampling_on}
                                    disabled={true}
                                />
                                <InputBox
                                    label={"Quantity*"}
                                    value={request.sample?.quantity?.toString()}
                                    disabled={true}
                                />
                            </div>
                            {request.sample?.extensions && (
                                <RequestInfoExtensionComponent extensions={request.sample?.extensions}/>
                            )}
                            <div className={style.content}>
                                <p className={style.title}>Memo</p>
                                <textarea
                                    className={style.memo}
                                    rows={8}
                                    value={request.memo}
                                    readOnly={true}
                                />
                            </div>
                        </div>
                    </div>
                ) : <Loading/>}
            </div>
        </div>
    )
}