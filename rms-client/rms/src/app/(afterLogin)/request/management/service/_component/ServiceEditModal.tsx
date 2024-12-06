'use client';

import React, {useEffect, useState} from "react";
import style from "@/app/(afterLogin)/request/management/service/_component/serviceEditModal.module.css";
import globalStyle from '@/css/modal.module.css';
import {faTrash, faXmark} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import InputBox from "@/app/_component/InputBox";
import SelectBox from "@/app/_component/SelectBox";
import {Categories} from "@/model/Categories";
import {getCategories} from "@/app/(afterLogin)/request/management/service/_api/getCategories";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import SelectSearchBox from "@/app/(afterLogin)/request/management/_component/SelectSearchBox";
import {Service} from "@/model/Service";
import {getService} from "@/app/(afterLogin)/_api/getService";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {patchService} from "@/app/(afterLogin)/request/management/service/_api/patchService";


type Props = {
    serviceId: string
    closeModal: () => void;
    refreshData: () => void;
}
const requiredOption = [
    {name: "False", value: false},
    {name: "True", value: true}
]
const requestTypes = [
    {name: "GENERAL", value: "GENERAL"},
    {name: "SET", value: "SET"}
]

export default function ServiceEditModal({serviceId, closeModal, refreshData}: Props) {
    const [service, setService] = useState<Service>();
    const [categories, setCategories] = useState<SelectBoxOption[]>([]);
    const [required, setRequired] = useState<SelectBoxOption>(requiredOption[0]);
    const [selectedSampleType, setSelectedSampleType] = useState<SelectBoxOption | null>(null);
    const [selectedExtension, setSelectedExtension] = useState<SelectBoxOption | null>(null);

    const fetchCategoryData = async () => {
        const response = await getCategories();
        const data = await response.json();
        setCategories(transformCategoryToOptions(data as Categories[]));
    }
    const transformCategoryToOptions = (data: Categories[]): SelectBoxOption[] => {
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };
    const fetchServiceData = async (serviceId: string) => {
        const response = await getService(serviceId);
        const data = await response.json();
        const service = data as Service
        setService(service);
        return service
    }

    const updateService = async () => {
        const response = await patchService(service!);
        if (response.ok) {
            alert("Update successful")
            closeModal()
            refreshData()
        } else alert("Fail update")
    }
    useEffect(() => {
        fetchCategoryData();
        fetchServiceData(serviceId);
    }, []);

    const handleSampleTypeAddClick = async () => {
        setService((prev) => {
            const isAlreadyAdded = prev?.sample_types?.some(
                (sampleType) => sampleType.id === selectedSampleType?.value
            );
            if (isAlreadyAdded) {
                alert("이미 등록되었습니다.")
                return prev;
            }
            return {
                ...prev,
                sample_types: [
                    ...(prev?.sample_types || []),
                    {
                        id: selectedSampleType?.value,
                        name: selectedSampleType?.name
                    }
                ]
            };
        });
    }

    const handleSampleTypeDeleteClick = async (sampleTypeId: string) => {
        setService((prev) => {
            return {
                ...prev,
                sample_types: (prev?.sample_types || []).filter(
                    (sampleType) => sampleType.id !== sampleTypeId
                )
            };
        });
    };

    const handleExtensionAddClick = async () => {
        setService((prev) => {
            const isAlreadyAdded = prev?.extensions?.some(
                (extension) => extension.id === selectedExtension?.value
            );
            if (isAlreadyAdded) {
                alert("이미 등록되었습니다.")
                return prev;
            }
            return {
                ...prev,
                extensions: [
                    ...(prev?.extensions || [] ),
                    {
                        id: selectedExtension?.value,
                        name: selectedExtension?.name,
                        required: required.value
                    }
                ]
            };
        });
    }

    const handleExtensionDeleteClick = async (extensionId: string) => {
        setService((prev) => {
            return {
                ...prev,
                extensions: (prev?.extensions || []).filter(
                    (extension) => extension.id !== extensionId
                )
            };
        });
    }

    return (
        <div className={globalStyle.modalBackground}>
            <div className={globalStyle.modal}>
                <FontAwesomeIcon icon={faXmark} onClick={closeModal} className={globalStyle.modalCloseButton}/>
                <div className={style.modalTitle}>Service Management</div>
                <div className={style.formGroup}>
                    <InputBox
                        label={"Name(KR)"}
                        value={service?.name_kr}
                        disabled={true}
                    />
                    <InputBox
                        label={"Code"}
                        value={service?.id}
                        disabled={true}
                    />
                </div>
                <div className={style.formGroup}>
                    <InputBox
                        label={"Name(EN)"}
                        value={service?.name}
                        disabled={false}
                        onChange={(value) => {
                            setService((prev) => ({
                                ...prev,
                                name: value
                            }))
                        }}
                    />
                    <SelectBox
                        width={'200px'}
                        label={"Category Name"}
                        value={service?.category?.name}
                        options={categories}
                        onChange={(value) => {
                            setService((prev) => ({
                                ...prev,
                                category: {
                                    id: value.value,
                                    name: value.name
                                }
                            }))
                        }}
                    />
                    <InputBox
                        label={"Group_name"}
                        value={service?.group_name}
                        disabled={false}
                        onChange={(value) => {
                            setService((prev) => ({
                                ...prev,
                                group_name: value && value.trim() !== "" ? value : null
                            }))
                        }}
                    />
                    <SelectBox
                        width={'200px'}
                        label={"Type"}
                        value={requestTypes[0].value}
                        options={requestTypes}
                        onChange={(value) => {
                            setService((prev) => ({
                                ...prev,
                                type: value.value
                            }))
                        }}
                    />
                </div>
                <div className={style.contentGroup}>
                    <div className={style.content}>
                        <div className={style.contentTitle}>Sample Type</div>
                        <div className={style.contentFormGroup}>
                            <SelectSearchBox
                                type={'sampleType'}
                                onSelect={setSelectedSampleType}
                                width={'220px'}
                            />
                            <button className={style.addButton} onClick={() => handleSampleTypeAddClick()}>Add</button>
                        </div>
                        <table className={style.table}>
                            <thead>
                            <tr>
                                <th>Code</th>
                                <th>Name</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            {service?.sample_types?.map((sampleType, index) => (
                                <tr key={index}>
                                    <td>{sampleType.id}</td>
                                    <td>{sampleType.name}</td>
                                    <td>
                                        <FontAwesomeIcon
                                            className={style.deleteButton}
                                            icon={faTrash}
                                            onClick={() => handleSampleTypeDeleteClick(sampleType.id)}
                                        />
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                    <div className={style.content}>
                        <div className={style.contentTitle}>Extension</div>
                        <div className={style.contentFormGroup}>
                            <SelectSearchBox type={'extension'} onSelect={setSelectedExtension} width={'220px'}/>
                            <div>
                                <SelectBox
                                    value={required.name}
                                    options={requiredOption}
                                    label={"required"}
                                    onChange={(selectedOption) => {
                                        setRequired(
                                            {
                                                name: selectedOption.name,
                                                value: selectedOption.value
                                            } as SelectBoxOption
                                        )
                                    }}
                                />
                            </div>
                            <button className={style.addButton} onClick={() => handleExtensionAddClick()}>Add</button>
                        </div>
                        <table className={style.table}>
                            <thead>
                            <tr>
                                <th>Code</th>
                                <th>Name</th>
                                <th>Required</th>
                                <th>Delete</th>
                            </tr>
                            </thead>
                            <tbody>
                            {service?.extensions?.map((extension, index) => (
                                <tr key={index}>
                                    <td>{extension.id}</td>
                                    <td>{extension.name}</td>
                                    <td>{extension.required ? "Yes" : "No"}</td>
                                    <td>
                                        <FontAwesomeIcon
                                            className={style.deleteButton}
                                            icon={faTrash}
                                            onClick={() => handleExtensionDeleteClick(extension.id!)}
                                        />
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                </div>
                <div className={style.buttonGroup}>
                    <GreenButton name={"Cancel"} onClick={closeModal}/>
                    <BlueButton name={'Confirm'} onClick={() => updateService()}/>
                </div>
            </div>
        </div>
    )
}