"use client"

import style from "@/app/(afterLogin)/request/order/_component/orderInfo.module.css";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {useRouter, useSearchParams} from "next/navigation";
import {useCallback, useEffect, useState} from "react";
import {Request} from "@/model/Request"
import InputBox from "@/app/_component/InputBox";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {getRequestOrderInfo} from "@/app/(afterLogin)/request/order/_api/getRequestOrderInfo";


export default function OrderInfo() {
    const [request, setRequest] = useState<Request>()
    const router = useRouter();
    const searchParams = useSearchParams()
    const serviceId = searchParams!.get("service")
    const sampleId = searchParams!.get("sample")

    const onClickClose = () => {
        router.back();
    };

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
        const handleKeyPress = (event: KeyboardEvent) => {
            if (event.key === 'Escape') {router.back();}
        };
        window.addEventListener('keydown', handleKeyPress);
        return () => {window.removeEventListener('keydown', handleKeyPress);};
    }, [fetchRequest, router]);

    return (
        <div className={style.modalBackground}>
            <div className={style.modal}>
                <div className={style.modalTitle}>
                    <h1>Order Details</h1>
                    <button onClick={onClickClose}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </button>
                </div>
                {request ? (
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
                        <div className={style.content}>
                            <p className={style.title}>Patient Info.</p>
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
                                label={"Date of Birth"}
                                value={getDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}
                                disabled={true}
                            />
                            <InputBox
                                label={"Age"}
                                value={request.sample?.age}
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
                                label={"Ward"}
                                value={request.ward}
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
                                label={"Date or collection*"}
                                value={request.sample?.sampling_on}
                                disabled={true}
                            />
                            <InputBox
                                label={"Quantity*"}
                                value={request.sample?.quantity?.toString()}
                                disabled={true}
                            />
                        </div>
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
                ) : <Loading/>}
            </div>
        </div>
    )
}