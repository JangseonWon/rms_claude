"use client"

import style from "@/app/(afterLogin)/request/result/resample/_component/requestModal.module.css";
import globalModalStyle from '@/css/modal.module.css';
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import React, {useCallback, useEffect, useMemo, useState} from "react";
import {Request} from "@/model/Request"
import InputBox from "@/app/_component/InputBox";
import {format} from "date-fns";
import DatePickerBox from "@/app/_component/DatePickerBox";
import TextBox from "@/app/_component/TextBox";
import BlueButton from "@/app/_component/BlueButton";
import ExtensionInputComponent from "@/app/(afterLogin)/request/result/resample/_component/ExtensionInputComponent";
import {putRequest} from "@/app/(afterLogin)/request/result/resample/_api/putRequest";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import {Status} from "@/model/Status";
import classNames from "classnames";
import scroll from "@/css/scrollBar.module.css";
import {getStringDateFromComponents} from "@/app/_component/DateUtil";
import {Extension} from "@/model/Extension";
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";

type Props = {
    selectedRequest: Request
    closeModal: () => void;
    refreshData: () => void;
}

export default function RequestModal({selectedRequest, closeModal,refreshData}: Props) {
    const showAlert = CallAlertDialog();
    const initialRequest = useMemo<Request>(() => ({
        user: { ...selectedRequest.user!},
        service: { ...selectedRequest.service },
        request_group: { ...selectedRequest.request_group },
        sample: {
            patient: { ...selectedRequest.sample!.patient! },
            sample_type: { ...selectedRequest.sample!.sample_type },
            extensions: []
        },
    }), [selectedRequest]);
    const [request, setRequest] = useState<Request>(initialRequest);
    const [extension, setExtension] = useState<Extension[]>([])

    useEffect(() => {
        fetchServiceExtensions(request.service!.id!)
            .then(res => res.json())
            .then((data: Extension[]) => {
                setExtension(data)
                // extensions 초기화
                setRequest(r => ({
                    ...r,
                    sample: {
                        ...r.sample!,
                        extensions: data.map(e => ({ id: e.id, value: undefined }))
                    }
                }))
            })
            .catch(() => {
                showAlert('Failed to load extensions')
            })
    }, [request.service])

    useEffect(() => {
        const sample = request.sample;
        const hasFullBirth = !!(sample?.patient?.birth_year && sample.patient.birth_month && sample.patient.birth_day);
        const hasSamplingOn = sample?.sampling_on != null;

        if (hasFullBirth && hasSamplingOn) {
            const birthDate = new Date(
                sample!.patient!.birth_year!,
                sample!.patient!.birth_month! - 1,
                sample!.patient!.birth_day!
            );
            const samplingDate = new Date(sample!.sampling_on!);
            const computed = setAge(birthDate, samplingDate);
            if (computed !== sample!.age) {
                setRequest(prev => ({
                    ...prev,
                    sample: { ...prev.sample!, age: computed }
                }));
            }
        } else {
            setRequest(prev => ({
                ...prev,
                sample: { ...prev.sample!, age: undefined }
            }));
        }
    }, [
        request.sample?.patient?.birth_year,
        request.sample?.patient?.birth_month,
        request.sample?.patient?.birth_day,
        request.sample?.sampling_on
    ]);


    const handleExtensionChange = useCallback((extId: string, value: any) => {
        setRequest(r => ({
            ...r,
            sample: {
                ...r.sample!,
                extensions: r.sample!.extensions!.map(e =>
                    e.id === extId ? { ...e, value } : e
                )
            }
        }))
    }, [])
    const handleProbandSelected = (proband: Request) => {
        setRequest(prev => ({
            ...prev,
            request_group: {id: proband.request_group?.id}
        }))
    }

    const handleRequestChange = useCallback(
        (path: string, value: unknown) => {
            setRequest(prev => setNestedValue(prev, path, value));
        },
        []
    );
    const handleOrderNow = useCallback(async () => {
        const updated: Request = {
            ...request,
            sample: {
                ...request.sample,
                extensions: [
                    ...(request.sample?.extensions ?? []),
                    {
                        id: "TA0093",
                        value: selectedRequest.sample?.barcode,
                    },
                ],
            },
            status: Status.UNCONFIRMED_ORDER,
            request_relation: { id: 2 },
        }
        try {
            const res = await putRequest(updated, selectedRequest.service!!.id!!, selectedRequest.sample!!.id!! )
            if (!res.ok) new Error()
            showAlert("success!")
            closeModal()
            refreshData()
        } catch {
            showAlert("fail!")
        }
    }, [request, closeModal, refreshData, showAlert]);

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

    const isAllRequiredFilled = useMemo(() => {
        const sample = request.sample
        if (!sample) return false
        const baseOK = Boolean(
            sample?.patient?.birth_year &&
            sample?.patient?.birth_month &&
            sample?.patient?.birth_day &&
            sample?.patient?.organization?.id &&
            sample?.patient?.name &&
            sample?.patient?.serial &&
            sample?.patient?.sex &&
            sample?.sample_type?.id &&
            sample?.sampling_on &&
            sample?.quantity &&
            new RegExp(`^\\d+$`).test(String(sample.quantity))
        )
        const exts = sample.extensions ?? []
        const extOK = extension
            .filter(e => e.required)
            .every(e => {
                const found = exts.find(x => x.id === e.id)
                return Boolean(found && found.value && found.value !== '')
            })
        const regexOK = exts.every(e => {
            const def = extension.find(s => s.id === e.id);
            if (!def || !e.value) return true;
            const pattern = new RegExp(`${def.regex}`);
            return pattern.test(String(e.value));
        });
        return baseOK && extOK && regexOK
    }, [request, extension])

    return (
        <div className={globalModalStyle.modalBackground}>
            <div className={globalModalStyle.modal} style={{width:"950px"}}>
                <div className={style.modalTitle}>
                    <h1>Re-sample Order</h1>
                    <button onClick={closeModal}>
                        <FontAwesomeIcon icon={faXmark}/>
                    </button>
                </div>
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
                            label={"Name *"}
                            value={request.sample?.patient?.name}
                            onChange={(value) => handleRequestChange('sample.patient.name', value)}
                            disabled={true}
                        />
                        <InputBox
                            label={"MRN *"}
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
                            label={"Type *"}
                            value={request.sample?.sample_type?.name}
                            disabled={true}
                        />
                        <DatePickerBox
                            label={"Collection Date *"}
                            required={true}
                            onChange={(date) => {
                                handleRequestChange('sample.sampling_on', format(date, "yyyy-MM-dd"))
                            }}
                        />
                        <InputBox
                            label={"Number of Specimens *"}
                            regex={"^\\d+$"}
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
                        <ExtensionInputComponent
                            request={request}
                            schema={extension}
                            extensions={request.sample!.extensions!}
                            onChange={handleExtensionChange}
                            onProbandSelected={handleProbandSelected}
                        />
                    </div>
                    <TextBox
                        label={'Memo'}
                        value={request.memo}
                        onChange={(value) => handleRequestChange('memo', value)}
                        placeholder={"Maximum 100 characters"}
                        lengthLimit={100}
                    />
                    <div className={style.flexEndContainer}>
                        <BlueButton
                            name={"Order now"}
                            disabled={!isAllRequiredFilled}
                            onClick={handleOrderNow}
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}