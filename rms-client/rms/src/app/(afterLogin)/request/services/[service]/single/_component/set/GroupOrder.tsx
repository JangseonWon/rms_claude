'use client';

import style from "@/app/(afterLogin)/request/services/[service]/single/_component/set/groupOrder.module.css";
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
import genomeImg from "../../../../../../../../../public/GCgenome_white.png";
import logo from "@/css/orderGenomeLogo.module.css";
import Image from "next/image";
import {Service} from "@/model/Service";
import {getServiceGroup} from "@/app/(afterLogin)/request/services/[service]/single/_api/getServiceGroup";
import {formatExtensionValue, setAge, setNestedValue} from './GroupOrderUtils';
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";

export default function GroupOrder() {
    const [organizationOptions, setOrganizationOptions] = useState<SelectBoxOption[]>([])
    const [sampleTypeOptions, setSampleTypeOptions] = useState<{ [key: string]: SelectBoxOption[] }>({});
    const [requests, setRequests] = useState<{ [key: string]: Request }>({});
    const [selectedOrganization, setSelectedOrganization] = useState<SelectBoxOption | null>(null);
    const [selectedSampleType, setSelectedSampleType] = useState<{ [index: number]: string }>({});
    const [selectedSex, setSelectedSex] = useState<{ [index: number]: string }>({});
    const [publishStatus, setPublishStatus] = useState<string | null>(null);
    const [isFilled, setIsFilled] = useState(false);
    const pathname = usePathname();
    const pathSegments = pathname.split('/');
    const serviceId = decodeURIComponent(pathSegments[pathSegments.length - 2]);
    const [serviceGroup, setServiceGroup] = useState<Service[]>([]);
    const [birthDates, setBirthDates] = useState<Date[]>([]);
    const showAlert = CallAlertDialog();
    const sexOption: SelectBoxOption[] = [
        { value: "M", name: "Male" },
        { value: "F", name: "Female" }
    ];

    const fetchOrganizations = useCallback(async () => {
        const response = await getOrganization();
        if (response.ok) {
            const data = await response.json();
            setOrganizationOptions(transformOrganizationToOptions(data as Organization[]));
        } else {
            setOrganizationOptions(transformOrganizationToOptions([]));
        }
    },[]);


    const fetchSampleTypesForServices = async (services: Service[]) => {
        const sampleTypePromises = services.map(async (service) => {
            const response = await getSampleType(service.id!);
            const data = await response.json();
            return { serviceId: service.id, sampleTypes: transformSampleTypeToOptions(data as SampleType[]) };
        });

        const results = await Promise.all(sampleTypePromises);
        const optionsByService = results.reduce((acc, { serviceId, sampleTypes }) => {
            acc[serviceId!] = sampleTypes;
            return acc;
        }, {} as { [key: string]: SelectBoxOption[] });

        setSampleTypeOptions(optionsByService);
    };

    useEffect(() => {
        const fetchInitialData = async () => {
            await fetchOrganizations();

            const response = await getServiceGroup(serviceId);
            if (response.ok) {
                const services = await response.json();
                setServiceGroup(services);
                await fetchSampleTypesForServices(services);
            } else {
                setServiceGroup([]);
            }
        };

        fetchInitialData();
    }, [serviceId, fetchOrganizations]);

    useEffect(() => {
        if (publishStatus) {
            publishRequests(publishStatus);
            setPublishStatus(null);
        }
    }, [publishStatus]);

    useEffect(() => {
        setIsFilled(isAllRequiredFilled());
    }, [requests]);

    const publishRequests = (status: string) => {
        const allRequests = Object.values(requests).map((req) => ({ ...req, status }));
        if (allRequests.length === 0) {
            showAlert("Error: No data available");
            return;
        }
        putRequest(allRequests)
            .then((res) => {
                if (res.ok) {
                    alert("All requests submitted successfully!");
                } else {
                    alert("Submission failed.");
                }
            })
            .catch((error) => console.error("Failed to submit requests:", error));
    };

    const handleRequestChange = (index: number, path: string, value: any) => {
        setRequests((prevRequests) => {
            const updatedRequests = { ...prevRequests };
            const currentRequest = { ...updatedRequests[index] };

            updatedRequests[index] = {
                ...currentRequest,
                ...setNestedValue(currentRequest, path, value),
            };

            return updatedRequests;
        });
    };

    const handleOrganizationChange = (organizationId: string | undefined, organizationName: string | undefined) => {
        if (!organizationId || !organizationName) return;

        setRequests((prevRequests) => {
            const updatedRequests = { ...prevRequests };

            Object.keys(updatedRequests).forEach((index) => {
                updatedRequests[+index] = {
                    ...updatedRequests[+index],
                    sample: {
                        ...updatedRequests[+index].sample,
                        patient: {
                            ...updatedRequests[+index].sample?.patient,
                            organization: {
                                id: organizationId,
                                name: organizationName,
                            },
                        },
                    },
                };
            });

            return updatedRequests;
        });
    };

    const handleExtensionChange = (index: number, id: string, value: any): void => {
        setRequests((prevRequests) => {
            const updatedRequests = { ...prevRequests };
            const currentRequest = { ...updatedRequests[index] };
            const existingExtensions = currentRequest.sample?.extensions || [];

            const existingExtensionIndex = existingExtensions.findIndex((ext) => ext.id === id);
            let updatedExtensions;

            if (existingExtensionIndex > -1) {
                updatedExtensions = [...existingExtensions];
                updatedExtensions[existingExtensionIndex] = { id, value: formatExtensionValue(value) };
            } else {
                updatedExtensions = [...existingExtensions, { id, value: formatExtensionValue(value) }];
            }

            currentRequest.sample = {
                ...currentRequest.sample,
                extensions: updatedExtensions,
            };

            updatedRequests[index] = currentRequest;
            return updatedRequests;
        });
    };

    const transformOrganizationToOptions = (data: Organization[]): SelectBoxOption[] =>
        data.map((org) => ({ value: org.id, name: org.name }));

    const transformSampleTypeToOptions = (data: SampleType[]): SelectBoxOption[] =>
        data.map((type) => ({ value: type.id, name: type.name }));



    const isAllRequiredFilled = () => {
        if (Object.keys(requests).length === 0) {
            return false;
        }

        return Object.values(requests).every((req) => {
            if (!req.sample?.patient?.name) return false;
            if (!req.sample.patient?.serial) return false;
            if (!req.sample.sample_type?.id) return false;
            if (!req.sample.sampling_on) return false;
            if (!req.sample.patient.sex) return false;
            if (!req.sample.patient.birth_year) return false;
            return req.sample.quantity;
        });
    };

    const groupOrderDiv = (service: Service, index:number) => {
        return (
            <div className={style.groupContainer}>
                <p className={style.title}>{service.name}</p>
                <p className={style.mainName}>Patient Info.</p>
                <div className={style.section}>
                    <InputBox
                        label={"Name*"}
                        required={true}
                        onChange={(value) =>{
                            handleRequestChange(index, `service.id`, service.id);
                            handleRequestChange(index,`sample.patient.name`, value);
                        }
                    }
                    />
                    <InputBox
                        label={"MRN*"}
                        required={true}
                        onChange={(value) => handleRequestChange(index,`sample.patient.serial`, value)}
                    />
                    <InputBox
                        label={"Age"}
                        disabled={true}
                        value={requests[index]?.sample?.age}
                    />
                    <div className={style.dateBox}>
                        <DatePickerBox
                            label={"Date of Birth"}
                            disable={!requests[index]?.sample?.patient?.name}
                            onChange={(date) => {
                                if (date) {
                                    setBirthDates(prevDates => [...prevDates, date]);
                                    handleRequestChange(index,`sample.patient.birth_year`, date.getFullYear());
                                    handleRequestChange(index,`sample.patient.birth_month`, date.getMonth() + 1);
                                    handleRequestChange(index,`sample.patient.birth_day`, date.getDate());
                                    if (requests[index].sample?.sampling_on) {
                                        const age = new Date(requests[index].sample!.sampling_on!);
                                        handleRequestChange(index, 'sample.age', setAge(date, age));
                                    }
                                } else {
                                    setBirthDates([]);
                                    handleRequestChange(index,`sample.patient.birth_year`, null);
                                    handleRequestChange(index,`sample.patient.birth_month`, null);
                                    handleRequestChange(index,`sample.patient.birth_day`, null);
                                    handleRequestChange(index,`sample.age`, null);
                                }
                            }}
                        />
                    </div>
                </div>
                <div className={style.section}>
                    <div className={style.selectBox}>
                        <SelectBox
                            label={"Sex*"}
                            value={selectedSex[index]}
                            options={sexOption}
                            required={true}
                            onChange={(value) => {
                                handleRequestChange(index,`sample.patient.sex`, value.value);
                                setSelectedSex((prev) => ({ ...prev, [index]: value.name }));
                            }}
                            width="200px"
                        />
                    </div>
                </div>
                <p className={style.mainName}>Specimen / Sample Info.</p>
                <div className={style.section}>
                    <div className={style.selectBox}>
                        <SelectBox
                            label={"Type*"}
                            value={selectedSampleType[index]}
                            options={sampleTypeOptions[service.id!]}
                            required={true}
                            onChange={(value) => {
                                handleRequestChange(index,`sample.sample_type.id`, value.value);
                                handleRequestChange(index,`sample.sample_type.name`, value.name);
                                setSelectedSampleType((prev) => ({ ...prev, [index]: value.name }));
                            }}
                            width="200px"
                        />
                    </div>
                    <div className={style.dateBox}>
                        <DatePickerBox
                            label={"Date of Collection*"}
                            disable={!requests[index]?.sample?.patient?.name}
                            required={true}
                            onChange={(date) => {
                                if (date) {
                                    handleRequestChange(index,`sample.sampling_on`, format(date, "yyyy-MM-dd"));
                                    if (birthDates) {
                                        const age = setAge(birthDates[index], date);
                                        handleRequestChange(index,'sample.age', age);
                                    }
                                } else {
                                    handleRequestChange(index,`sample.sampling_on`, null);
                                    handleRequestChange(index,`sample.age`, null);
                                }
                            }}
                        />
                    </div>
                    <InputBox
                        label={"Quantity*"}
                        required={true}
                        onChange={(value) => handleRequestChange(index,`sample.quantity`, value)}
                    />
                </div>
                <p className={style.mainName}>Additional Info.</p>
                <div className={style.section}>
                    <InputBox
                        label={"Medical Department"}
                        onChange={(value) => handleRequestChange(index,`department`, value)}
                    />
                    <InputBox
                        label={"Ward"}
                        onChange={(value) => handleRequestChange(index,`ward`, value)}
                    />
                    <InputBox
                        label={"Physician Name"}
                        onChange={(value) => handleRequestChange(index,`physician`, value)}
                    />
                </div>
                <div className={style.extensionSection}>
                    <ExtensionInputComponent serviceId={service.id!} onChange={(id, value) => handleExtensionChange(index, id, value)}/>
                </div>
                <div className={style.memoSection}>
                    <TextBox
                        label={"Memo"}
                        value={requests[index]?.memo}
                        required={true}
                        onChange={(value) => handleRequestChange(index,`memo`, value)}
                    />
                </div>
            </div>
        );
    }

    return (
        <div className={style.container}>
            <Image className={logo.genomeImg} src={genomeImg} alt={"genome"}/>
            <div className={style.buttonSection}>
                <GreenButton
                    name={"Add to Cart"}
                    disabled={!isFilled}
                    onClick={() => {
                        handleOrganizationChange(selectedOrganization?.value, selectedOrganization?.name);
                        setPublishStatus("CART");
                    }
                }
                />
                <BlueButton
                    name={"Order Now"}
                    disabled={!isFilled}
                    onClick={() => {
                        handleOrganizationChange(selectedOrganization?.value, selectedOrganization?.name);
                        setPublishStatus("UNCONFIRMED_ORDER");
                    }
                }
                />
            </div>
            <p className={style.mainName}>Institution name *</p>
            <div className={style.section}>
                <div className={style.selectBox}>
                    <SelectBox
                        label={""}
                        options={organizationOptions}
                        value={selectedOrganization?.name}
                        required={true}
                        onChange={(value) => {
                            setSelectedOrganization(value);
                        }}
                        width="200px"
                    />
                </div>
            </div>
            {serviceGroup && serviceGroup.length > 0 && serviceGroup.map((service, index) =>
                groupOrderDiv(service, index))
            }
        </div>
    )
}