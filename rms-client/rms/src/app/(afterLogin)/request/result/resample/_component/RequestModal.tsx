"use client"

import style from "@/app/(afterLogin)/request/result/resample/_component/requestModal.module.css";
import globalModalStyle from '@/css/modal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useEffect} from "react";
import {Request} from "@/model/Request"
import InputBox from "@/app/_component/InputBox";
import Loading from "@/app/(afterLogin)/_component/Loading";
import {useRequestStore} from '@/store/useRequestStore';
import {format} from "date-fns";
import DatePickerBox from "@/app/_component/DatePickerBox";
import TextBox from "@/app/_component/TextBox";
import BlueButton from "@/app/_component/BlueButton";
import ExtensionInputComponent from "@/app/(afterLogin)/request/result/resample/_component/ExtensionInputComponent";
import {putRequest} from "@/app/(afterLogin)/request/result/resample/_api/putRequest";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {Status} from "@/model/Status";
import {getRequest} from "@/app/(afterLogin)/request/result/resample/_api/getRequest";
import classNames from "classnames";
import scroll from "@/css/scrollBar.module.css";
import {getStringDateFromComponents} from "@/app/_component/DateUtil";

type Props = {
    propRequest: Request | undefined
    closeModal: () => void;
    refreshData: () => void;
}

export default function RequestModal({propRequest, closeModal,refreshData}: Props) {
    const { request, setRequest } = useRequestStore();
    const showAlert = CallAlertDialog();

    const handleRequestChange = (path: string, value: any) => {
        setRequest(prevState => ({
            ...prevState,
            ...setNestedValue({ ...prevState }, path, value)
        }));
    };
    const handleOrderNow = async() =>{
        const updatedRequest: Request = {
            ...request,
            status: Status.UNCONFIRMED_ORDER,
            request_relation: {
                id:2
            }
        };
        const response = await putRequest(updatedRequest!)
        if(response.ok){
            closeModal();
            refreshData();
            showAlert("success!")
        }else{
            showAlert("fail!")
        }
    }

    const setNestedValue = (object: any, nestedPath: string, newValue: any): any => {
        const [firstKey, ...remainingPathSegments] = nestedPath.split('.');
        if (remainingPathSegments.length === 0) {
            return { ...object, [firstKey]: newValue };
        }
        return {
            ...object,
            [firstKey]: setNestedValue(object[firstKey] || {}, remainingPathSegments.join('.'), newValue),
        };
    };
    const setAge = (birthDate: Date, samplingDate: Date): number => {
        let age = samplingDate.getFullYear() - birthDate.getFullYear();
        const monthDifference = samplingDate.getMonth() - birthDate.getMonth()
        if (monthDifference < 0 || (monthDifference === 0 && samplingDate.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    };
    const fetchRequest = async () => {
        const response = await getRequest(propRequest!.sample!.id!, propRequest!.service!.id!)
        const json = await response.json()
        setRequest(json as Request)
    };

    useEffect(() => {
        fetchRequest()
    }, []);

    const isAllRequiredFilled = () => {
        const sample = request?.sample;
        if (!sample) return false;
        const requiredFields = [
            sample?.patient?.organization?.id,
            sample?.patient?.name,
            sample?.patient?.serial,
            sample?.patient?.sex,
            sample?.sample_type?.id,
            sample?.sampling_on,
            sample?.quantity,
        ];
        if (requiredFields.some(field => typeof field !== 'string' || field.trim() === '')) return false;
        return (sample.extensions || []).every(
            extension =>
                !extension.required || (extension.value != null && extension.value !== '')
        );
    };

    return (
        <div className={globalModalStyle.modalBackground}>
            <div className={globalModalStyle.modal}>
                <div className={style.modalTitle}>
                    <h1>Re-sample Order</h1>
                    <button onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </button>
                </div>
                {request ? (
                    <div className={classNames(style.wrapper, scroll.default)}>
                        <p className={style.contentTitle}>Institution name</p>
                        <div className={style.flexStartContainer}>
                            <InputBox
                                label={"Institution"}
                                value={request.sample?.patient?.organization?.name}
                                disabled={true}
                            />

                        </div>
                        <p className={style.contentTitle}>Service Info.</p>
                        <div className={style.flexStartContainer}>
                            <InputBox
                                label={"Service"}
                                value={request.service?.name}
                                disabled={true}
                            />
                        </div>
                        <p className={style.contentTitle}>Patient Info.</p>
                        <div className={style.flexStartContainer}>
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
                                value={getStringDateFromComponents(request.sample?.patient?.birth_year, request.sample?.patient?.birth_month, request.sample?.patient?.birth_day)}
                                disabled={true}
                            />
                            <InputBox
                                label={"Age"}
                                value={request.sample?.age}
                                disabled={true}
                            />
                        </div>
                        <p className={style.contentTitle}>Specimen/.Sample Info.</p>
                        <div className={style.flexStartContainer}>
                            <InputBox
                                label={"Type*"}
                                value={request.sample?.sample_type?.name}
                                disabled={true}
                            />
                            <DatePickerBox
                                label={"Collection Date*"}
                                required={true}
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
                                label={"Number of Specimens*"}
                                regex={"-?\\d+"}
                                required={true}
                                onChange={(value) => handleRequestChange('sample.quantity', value)}
                            />
                        </div>
                        <p className={style.contentTitle}>Additional Info.</p>
                        <div className={style.flexStartContainer}>
                            <InputBox
                                label={"Medical Department"}
                                onChange={(value) => handleRequestChange('department', value)}
                            />
                            <InputBox
                                label={"Physician Name"}
                                onChange={(value) => handleRequestChange('physician', value)}
                            />
                        </div>
                        <div className={style.flexStartContainer}>
                            <ExtensionInputComponent serviceId={request.service?.id!}/>
                        </div>
                        <TextBox
                            label={'Memo'}
                            value={request.memo}
                            onChange={(value) => handleRequestChange('memo', value)}
                        />
                        <div className={style.flexEndContainer}>
                            <BlueButton
                                name={"Order now"}
                                disabled={!isAllRequiredFilled()}
                                onClick={() => handleOrderNow()}
                            />
                        </div>
                    </div>
                ) : <Loading/>}
            </div>
        </div>
    )
}