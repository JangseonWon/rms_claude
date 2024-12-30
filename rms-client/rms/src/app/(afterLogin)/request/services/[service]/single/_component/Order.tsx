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
import {getSampleType} from "@/app/(afterLogin)/request/services/_api/getSampleType"
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {putRequest} from "@/app/(afterLogin)/request/services/[service]/single/_api/putRequest";
import {format} from "date-fns";
import {usePathname} from "next/navigation";
import ExtensionInputComponent
    from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/ExtensionInputComponent";
import TextBox from "@/app/_component/TextBox";
import genomeImg from "@/../public/GCgenome_white.png";
import logo from "@/css/orderGenomeLogo.module.css";
import Image from "next/image";
import SearchProbandModal from "@/app/(afterLogin)/request/services/[service]/single/_component/extension/SearchProbandModal";
import {
    useProbandModalOpen,
    useSetProbandModalOpen
} from "@/app/(afterLogin)/request/services/[service]/single/store/useProbandStore";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

export default function Order() {
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<SelectBoxOption[]>([])
    const [request, setRequest] = useState<Request>({})
    const [selectedOrganization, setSelectedOrganization] = useState<SelectBoxOption | null>(null);
    const [selectedSampleType, setSelectedSampleType] = useState<SelectBoxOption | null>(null);
    const [selectedSex, setSelectedSex] = useState<SelectBoxOption | null>(null);
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const probandModal = useProbandModalOpen();
    const setProbandModal = useSetProbandModalOpen();
    const showAlert = CallAlertDialog();
    const sexOption: SelectBoxOption[] = [
        { value: "M", name: "Male" },
        { value: "F", name: "Female" }
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
    }, [fetchOrganizations]);

    const publishRequest = (status:string) => {
        const updateRequest = [{
            ...request, ...{status: status}
        }]
        putRequest(updateRequest)
            .then((res) =>{
                if(res.ok) {
                    showAlert("success!", true);
                }
                else showAlert("fail");
            })
    };

    const handleRequestChange = (path: string, value: any):void => {
        setRequest(prevState => ({
            ...prevState,
            ...setNestedValue({...prevState}, path, value)
        }));
    };

    const handleExtensionChange = (id: string, value: any): void => {
        setRequest(prevState => {
            const existingExtensions = prevState.sample?.extensions || [];

            const existingExtensionIndex = existingExtensions.findIndex(ext => ext.id === id);

            let updatedExtensions;

            if (existingExtensionIndex > -1) {
                updatedExtensions = [...existingExtensions];
                updatedExtensions[existingExtensionIndex] = { id, value: formatExtensionValue(value) };
            } else {
                updatedExtensions = [...existingExtensions, { id, value: formatExtensionValue(value) }];
            }
            console.log(updatedExtensions);

            return {
                ...prevState,
                sample: {
                    ...prevState.sample,
                    extensions: updatedExtensions
                }
            };
        });
    };

    const formatExtensionValue = (value: any) => {
        if (typeof value === 'object' && value !== null && 'name' in value && 'value' in value) {
            return value.value;
        }

        if (typeof value === 'boolean') {
            return value;
        }

        return value;
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

    const Close = () => {
        setProbandModal(false);
    }

    return (
        <div className={style.container}>
            <Image className={logo.genomeImg} src={genomeImg} alt={"genome"}/>
            <div className={style.buttonSection}>
                <GreenButton
                    name={"Add to Cart"}
                    disabled={!isAllRequiredFilled()}
                    onClick={() => publishRequest("CART")}
                />
                <BlueButton
                    name={"Order Now"}
                    disabled={!isAllRequiredFilled()}
                    onClick={() => publishRequest("UNCONFIRMED_ORDER")}
                />
            </div>
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
                        width="200px"
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
                <InputBox
                    label={"Age"}
                    disabled={true}
                    value={request.sample?.age}
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
            </div>
            <div className={style.section}>
                <div className={style.selectBox}>
                    <SelectBox
                        label={"Sex*"}
                        value={selectedSex}
                        options={sexOption}
                        required={true}
                        onChange={(value) => {
                            handleRequestChange('sample.patient.sex', value.value)
                            setSelectedSex(value.name);
                        }}
                        width="200px"
                    />
                </div>
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
                        width="200px"
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
            <ExtensionInputComponent serviceId={serviceId} onChange={handleExtensionChange}/>
            <div className={style.memoSection}>
                <TextBox
                    label={'Memo'}
                    value={request.memo}
                    required={true}
                    onChange={(value) => handleRequestChange('memo', value)}
                />
            </div>
            {probandModal && (
                <SearchProbandModal closeModal={Close}/>
            ) }
        </div>
    )
}