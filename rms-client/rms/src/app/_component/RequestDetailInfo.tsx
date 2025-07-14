"use client"

import style from "./requestDetailInfo.module.css";
import globalStyle from '@/css/modal.module.css';
import scrollbar from "@/css/scrollBar.module.css";
import {faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import SelectBox from "@/app/_component/SelectBox"
import React, {useCallback, useEffect, useMemo, useState} from "react";
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
import {Query} from "@/model/Query";
import {getDateFromComponents} from "@/app/_component/DateUtil";
import RequestDetailInfoExtension from "@/app/_component/RequestDetailInfoExtension";
import {searchRequests} from "@/app/(afterLogin)/_api/searchRequests";
import {Extension} from "@/model/Extension";
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";

type Props = {
    selectedRequest: Request
    closeModal: () => void;
    editable: boolean;
}

export default function RequestDetailInfo({editable, selectedRequest, closeModal}: Props) {
    const showAlert = CallAlertDialog();
    const [request, setRequest] = useState<Request>(selectedRequest)
    const [schema, setSchema] = useState<Extension[]>([])
    const [rootRequest, setRootRequest] = useState<Request | undefined>(undefined)
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<SelectBoxOption[]>([]);

    const sexOption: SelectBoxOption[] = [
        { value: "M", name: "M" },
        { value: "F", name: "F" }
    ];

    const fetchOrganizations = useCallback(async () => {
        const response = await getOrganization(request.user!.id!);
        if (response.ok) {
            const data = await response.json();
            setOrganizationOptions(transformOrganizationsToOptions(data as Organization[]));
        }
    },[request.user!.id!]);

    const fetchSampleType = useCallback(async () => {
        const response = await getSampleType(request.service!.id!)
        const json = await response.json()
        setSampleTypeOptions(transformSampleTypeToOptions(json as SampleType[]))
    },[request.service!.id!])


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
                            value: request.request_group!.id!,
                            operator: "="
                        },
                        {
                            table: "request",
                            column: "sample_id",
                            value: request.sample!.id!,
                            operator: "!="
                        },
                        {
                            table: "request",
                            column: "request_relation_id",
                            value: "1",
                            operator: "="
                        }
                    ]
                }
            ]
        }
        const response = await searchRequests(query).then(r => r.json())
        const rootRequest: Request = response.find((req: Request) => req.request_relation?.id === 1) || null;
        setRootRequest(rootRequest);
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
        if (isAllRequiredFilled) {
            const response = await updateRequest(request);
            if (response.ok) showAlert("Success update");
            else showAlert("Fail update");
        } else {
            showAlert("Please fill out all required fields.");
        }
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
        const extOK = schema
            .filter(e => e.required)
            .every(e => {
                const found = exts.find(x => x.id === e.id)
                return Boolean(found && found.value && found.value !== '')
            })
        const regexOK = exts.every(e => {
            const def = schema.find(s => s.id === e.id);
            if (!def || !e.value) return true;
            const pattern = new RegExp(`${def.regex}`)
            return pattern.test(String(e.value));
        });
        return baseOK && extOK && regexOK
    }, [request, schema])

    const setAge = (birthDate: Date, samplingDate: Date): number => {
        let age = samplingDate.getFullYear() - birthDate.getFullYear();
        const monthDifference = samplingDate.getMonth() - birthDate.getMonth()
        if (monthDifference < 0 || (monthDifference === 0 && samplingDate.getDate() < birthDate.getDate())) {
            age--;
        }
        return age;
    };
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
    useEffect(() => {
        fetchRequest()
        fetchOrganizations()
        fetchSampleType()
        fetchServiceExtensions(request.service!.id!)
            .then(res => res.json())
            .then((data: Extension[]) => {
                setSchema(data)
            })
            .catch(err => {
                showAlert('Failed to load extensions')
            })
    }, [fetchRequest, fetchOrganizations, fetchSampleType]);

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

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <div className={style.modalTitle}>
                    <h1>Details</h1>
                    <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={globalStyle.modalCloseButton}/>
                </div>
                {request && rootRequest !== undefined ? (
                    <div className={classNames(style.modalContent, scrollbar.default)}>
                        <p className={style.title}>Institution name*</p>
                        <div className={style.gridContainer}>
                            <SelectBox
                                disabled={editable}
                                label={""}
                                value={request.sample?.patient?.organization?.name!}
                                options={organizationOptions}
                                onChange={(value) => {
                                    handleRequestChange('sample.patient.organization.id', value.value)
                                    handleRequestChange('sample.patient.organization.name', value.name)
                                }}
                            />
                        </div>
                        <p className={style.title}>Service Info.</p>
                        <div className={style.gridContainer}>
                            <InputBox
                                label={"Service"}
                                value={request.service?.name}
                                disabled={true}
                            />
                        </div>
                        <p className={style.title}>Patient Info.</p>
                        <div className={style.gridContainer}>
                            <InputBox
                                disabled={editable}
                                label={"Name*"}
                                value={request.sample?.patient?.name}
                                onChange={(value) => handleRequestChange('sample.patient.name', value)}
                                required={editable}
                            />
                            <InputBox
                                disabled={editable}
                                label={"MRN*"}
                                value={request.sample?.patient?.serial}
                                onChange={(value) => handleRequestChange('sample.patient.serial', value)}
                                required={editable}
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
                                    } else {
                                        handleRequestChange('sample.patient.birth_year', null);
                                        handleRequestChange('sample.patient.birth_month', null);
                                        handleRequestChange('sample.patient.birth_day', null);
                                    }
                                }}
                                disable={editable!}
                            />
                            <SelectBox
                                disabled={editable}
                                label={"Gender*"}
                                value={request.sample?.patient?.sex!}
                                options={sexOption}
                                required={true}
                                onChange={(value) => {
                                    handleRequestChange('sample.patient.sex', value.value)
                                }}
                                width="200px"
                            />
                        </div>
                        <p className={style.title}>Specimen/.Sample Info.</p>
                        <div className={style.gridContainer}>
                            <SelectBox
                                disabled={editable}
                                label={"Type*"}
                                value={request.sample?.sample_type?.name!}
                                options={sampleTypeOptions}
                                onChange={(value) => {
                                    handleRequestChange('sample.sample_type.id', value.value)
                                    handleRequestChange('sample.sample_type.name', value.name)
                                }}
                            />
                            <DatePickerBox
                                label={"Collection Date*"}
                                value={request.sample?.sampling_on}
                                onChange={(date) => {
                                    handleRequestChange('sample.sampling_on', format(date, "yyyy-MM-dd"))
                                }}
                                disable={editable!}
                            />
                            <InputBox
                                disabled={editable}
                                label={"Number of Specimens*"}
                                regex={"^\\d+$"}
                                value={request.sample?.quantity?.toString()}
                                required={true}
                                onChange={(value) => handleRequestChange('sample.quantity', value)}
                            />
                        </div>
                        {(request.courier_company || request.awb_number) && (
                            <>
                                <p className={style.title}>Aviation Info.</p>
                                <div className={style.gridContainer}>
                                    <InputBox
                                        disabled={editable}
                                        label={"Global courier"}
                                        value={request.courier_company ? request.courier_company : '-'}
                                        onChange={(value) => handleRequestChange('courier_company', value)}
                                    />
                                    <InputBox
                                        disabled={editable}
                                        label={"AirWaybill no."}
                                        value={request.awb_number ? request.awb_number : '-'}
                                        onChange={(value) => handleRequestChange('awb_number', value)}
                                    />
                                </div>
                            </>
                        )}
                        <p className={style.title}>Additional Info.</p>
                        <div className={style.gridContainer}>
                            <InputBox
                                disabled={editable}
                                label={"Medical Department"}
                                value={request.department}
                                onChange={(value) => handleRequestChange('department', value)}
                            />
                            <InputBox
                                disabled={editable}
                                label={"Physician Name"}
                                value={request.physician}
                                onChange={(value) => handleRequestChange('physician', value)}
                            />
                        </div>
                        {request.sample?.extensions && (
                            <RequestDetailInfoExtension
                                disabled={editable}
                                request={request}
                                rootRequest={rootRequest}
                                schema={schema}
                                extensions={request.sample.extensions}
                                onChange={handleExtensionChange}
                                onProbandSelected={handleProbandSelected}
                            />
                        )}
                        <div>
                            <TextBox
                                disabled={editable}
                                label={'Memo'}
                                value={request.memo}
                                onChange={(value) => handleRequestChange('memo', value)}
                            />
                        </div>
                        {rootRequest && (
                            <>
                                <p className={style.title}>History</p>
                                <div className={style.gridContainer}>
                                    <InputBox
                                        disabled={true}
                                        label={"Registration ID"}
                                        value={rootRequest.sample?.barcode}
                                        onChange={(value) => handleRequestChange('department', value)}
                                    />
                                    <InputBox
                                        disabled={true}
                                        label={"MRN"}
                                        value={rootRequest.sample?.patient?.serial}
                                        onChange={(value) => handleRequestChange('physician', value)}
                                    />
                                </div>
                            </>
                        )}
                        {!editable && (
                            <div className={style.modalBottom}>
                                <GreenButton
                                    name={"Edit"}
                                    onClick={handleEditClick}
                                    disabled={!isAllRequiredFilled}
                                />
                            </div>
                        )}
                    </div>
                ) : <Loading/>}
            </div>
        </div>
    )
}