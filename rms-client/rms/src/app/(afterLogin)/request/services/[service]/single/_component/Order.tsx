'use client';

import style from "@/app/(afterLogin)/request/services/[service]/single/_component/order.module.css"
import SelectBox from "@/app/_component/SelectBox";
import InputBox from "@/app/_component/InputBox";
import React, {useCallback, useEffect, useMemo, useState} from "react";
import {Organization} from "@/model/Organization";
import {getOrganization} from "@/app/(afterLogin)/request/services/[service]/single/_api/getOrganization";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import DatePickerBox from "@/app/_component/DatePickerBox";
import {SampleType} from "@/model/SampleType";
import {getSampleType} from "@/app/(afterLogin)/request/services/_api/getSampleType"
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {format} from "date-fns";
import {usePathname} from "next/navigation";
import ExtensionInputComponent
    from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/ExtensionInputComponent";
import TextBox from "@/app/_component/TextBox";
import genomeImg from "@/../public/GCgenome_white.png";
import logo from "@/css/orderGenomeLogo.module.css";
import Image from "next/image";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import type {Request} from "@/model/Request";
import {Extension, ExtensionType} from "@/model/Extension";
import {fetchServiceExtensions} from "@/app/(afterLogin)/request/services/_api/fetchServiceExtensions";
import {Status} from "@/model/Status";
import {putRequest} from "@/app/(afterLogin)/request/services/[service]/single/_api/putRequest";

export default function Order() {
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<SelectBoxOption[]>([])
    const [request, setRequest] = useState<Request>({})
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [schema, setSchema] = useState<Extension[]>([])
    const showAlert = CallAlertDialog();
    const sexOption: SelectBoxOption[] = [
        { value: "M", name: "M" },
        { value: "F", name: "F" }
    ];

    const fetchOrganizations = useCallback(async () => {
        const response = await getOrganization()
        if (response.ok) {
            const data = await response.json();
            setOrganizationOptions(transformOrganizationToOptions(data as Organization[]));
        } else {
            setOrganizationOptions(transformOrganizationToOptions([]));
        }
    },[]);


    const fetchSampleType = async (serviceId: string) => {
        const response = await getSampleType(serviceId)
        if (response.ok) {
            const data = await response.json();
            setSampleTypeOptions(transformSampleTypeToOptions(data as SampleType[]));
        } else {
            setSampleTypeOptions(transformSampleTypeToOptions([]));
        }
    }

    useEffect(() => {
        fetchOrganizations();
        fetchSampleType(serviceId);
        handleRequestChange('service.id', serviceId);
        fetchServiceExtensions(serviceId)
            .then(res => res.json())
            .then((data: Extension[]) => {setSchema(data)});
    }, []);

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

    const publishRequest = (status:string) => {
        const hasProband = schema.some(s => s.type === ExtensionType.PROBAND_LIST || s.type === ExtensionType.PROBAND_SEARCH);
        const updateRequest: Request[] = [{
            ...request,
            ...{status: status},
            ...(hasProband ? {
                request_relation: {id: 3}
            } : {})
        }]
        putRequest(updateRequest)
            .then((res) =>{
                if(res.ok) {
                    showAlert("success!", true);
                }
                else showAlert("fail");
            })
    };
    const handleProbandSelected = (proband: Request) => {
        setRequest(prev => ({
            ...prev,
            request_group: {id: proband.request_group?.id}
        }))
    }
    const handleExtensionChange = useCallback((extId: string, value: string) => {
        setRequest(r => {
            const sample = r.sample ?? {};
            const exts = sample.extensions ?? [];

            const exists = exts.some(e => e.id === extId);
            const newExts = exists
                ? exts.map(e =>
                    e.id === extId
                        ? { ...e, value }
                        : e
                )
                : [...exts, { id: extId, value }];

            return {
                ...r,
                sample: {
                    ...sample,
                    extensions: newExts
                }
            };
        });
    }, []);

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
        const mDiff = samplingDate.getMonth() - birthDate.getMonth();
        if (mDiff < 0 || (mDiff === 0 && samplingDate.getDate() < birthDate.getDate())) age--;
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
        const extOK = schema
            .filter(e => e.required)
            .every(e => {
                const found = exts.find(x => x.id === e.id)
                return Boolean(found && found.value && found.value !== '')
            })
        const regexOK = exts.every(e => {
            const def = schema.find(s => s.id === e.id);
            if (!def || !e.value) return true;
            const pattern = new RegExp(`^${def.regex}$`);
            return pattern.test(String(e.value));
        });
        return baseOK && extOK && regexOK
    }, [request, schema])

    return (
        <div className={style.container}>
            <Image className={logo.genomeImg} src={genomeImg} alt={"genome"}/>
            <div className={style.buttonSection}>
                <GreenButton
                    name={"Add to Cart"}
                    disabled={!isAllRequiredFilled}
                    onClick={() => publishRequest("CART")}
                />
                <BlueButton
                    name={"Order Now"}
                    disabled={!isAllRequiredFilled}
                    onClick={() => publishRequest(Status.UNCONFIRMED_ORDER)}
                />
            </div>
            <p className={style.mainName}>Institution name *</p>
            <div className={style.section}>
                <div className={style.selectBox}>
                    <SelectBox
                        label={""}
                        options={organizationOptions}
                        value={request?.sample?.patient?.organization?.name}
                        required={true}
                        onChange={(value) => {
                            handleRequestChange('sample.patient.organization.id', value.value)
                            handleRequestChange('sample.patient.organization.name', value.name)
                        }}
                        width="300px"
                    />
                </div>
            </div>
            <p className={style.mainName}>Patient Info.</p>
            <div className={style.section}>
                <InputBox
                    label={"Name *"}
                    required={true}
                    onChange={(value) => handleRequestChange('sample.patient.name', value)}
                />
                <InputBox
                    label={"MRN *"}
                    required={true}
                    onChange={(value) => handleRequestChange('sample.patient.serial', value)}
                />
                <InputBox
                    label={"Age"}
                    disabled={true}
                    value={request?.sample?.age}
                />
                <div className={style.dateBox}>
                    <DatePickerBox
                        label={"Date of Birth *"}
                        required={true}
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
                    />
                </div>
            </div>
            <div className={style.section}>
                <div className={style.selectBox}>
                    <SelectBox
                        label={"Gender *"}
                        value={request?.sample?.patient?.sex}
                        options={sexOption}
                        required={true}
                        onChange={(value) => {
                            handleRequestChange('sample.patient.sex', value.value)
                        }}
                        width="200px"
                    />
                </div>
            </div>
            <p className={style.mainName}>Specimen/ .Sample Info.</p>
            <div className={style.section}>
                <div className={style.selectBox}>
                    <SelectBox
                        label={"Type *"}
                        value={request?.sample?.sample_type?.name}
                        options={sampleTypeOptions}
                        required={true}
                        onChange={(value) => {
                            handleRequestChange('sample.sample_type.id', value.value)
                            handleRequestChange('sample.sample_type.name', value.name)
                        }}
                        width="200px"
                    />
                </div>
                <div className={style.dateBox}>
                    <DatePickerBox
                        label={"Collection Date *"}
                        required={true}
                        onChange={(date) => {
                            handleRequestChange('sample.sampling_on', format(date, "yyyy-MM-dd"))
                        }}
                    />
                </div>
                <InputBox
                    label={"Number of Specimens *"}
                    required={true}
                    regex={"\\d+"}
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
                    label={"Physician Name"}
                    onChange={(value) => handleRequestChange('physician', value)}
                />
            </div>
            <ExtensionInputComponent
                request={request}
                schema={schema}
                onChange={handleExtensionChange}
                onProbandSelected={handleProbandSelected}
            />
            <TextBox
                label={'Memo'}
                value={request?.memo}
                onChange={(value) => handleRequestChange('memo', value)}
            />
        </div>
    )
}