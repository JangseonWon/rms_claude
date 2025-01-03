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
import {Service} from "@/model/Service";
import {getService} from "@/app/(afterLogin)/_api/getService";
import GreenButton from "@/app/_component/GreenButton";
import BlueButton from "@/app/_component/BlueButton";
import {patchService} from "@/app/(afterLogin)/request/management/service/_api/patchService";
import {CallAlertDialog} from "@/app/_component/dialog/CallAlertDialog";
import SearchSelectBox, {Option} from "@/app/_component/SearchSelectBox";
import {SampleType} from "@/model/SampleType";
import {getSampleTypes} from "@/app/(afterLogin)/request/management/service/_api/getSampleTypes";
import {getExtensions} from "@/app/(afterLogin)/request/management/service/_api/getExtensions";
import {Extension} from "@/model/Extension";


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
    const [sampleTypeOptions, setSampleTypeOptions] = useState<Option[]>([])
    const [extensionOptions, setExtensionOptions] = useState<Option[]>([])
    const [required, setRequired] = useState<SelectBoxOption>(requiredOption[0]);
    const [selectedSampleType, setSelectedSampleType] = useState<Option| undefined>();
    const [selectedExtension, setSelectedExtension] = useState<Option | undefined>();
    const [searchSampleType, setSearchSampleType] = useState('');
    const [searchExtension, setSearchExtension] = useState('');
    const showAlert = CallAlertDialog();

    const fetchCategoryData = async () => {
        const response = await getCategories();
        if (response.ok) {
            const data = await response.json();
            setCategories(transformCategoryToOptions(data as Categories[]));
        }
    }
    const fetchSampleTypeData = async () => {
        const response = await getSampleTypes();
        if (response.ok) {
            const data = await response.json();
            const sampleTypes = data as SampleType[]
            const mappedOptions = sampleTypes.map(mapSampleTypeToOption)
            setSampleTypeOptions(mappedOptions)
        }
    }
    const mapSampleTypeToOption = (sampleType: SampleType): Option => ({
        id: sampleType.id!,
        label: sampleType.name!
    });
    const fetchExtensionData = async () => {
        const response = await getExtensions();
        if (response.ok) {
            const data = await response.json();
            const extensions = data as Extension[]
            const mappedOptions = extensions.map(mapExtensionToOption)
            setExtensionOptions(mappedOptions)
        }
    }
    const mapExtensionToOption = (extension: Extension): Option => ({
        id: extension.id!,
        label: extension.name!
    });
    const transformCategoryToOptions = (data: Categories[]): SelectBoxOption[] => {
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };
    const fetchServiceData = async (serviceId: string) => {
        const response = await getService(serviceId);
        if (response.ok) {
            const data = await response.json();
            const service = data as Service
            setService(service);
            return service
        } else {
            return []
        }
    }

    const updateService = async () => {
        const response = await patchService(service!);
        if (response.ok) {
            showAlert("Update successful")
            closeModal()
            refreshData()
        } else showAlert("Fail update")
    }
    useEffect(() => {
        fetchCategoryData();
        fetchSampleTypeData();
        fetchExtensionData();
        fetchServiceData(serviceId);
    }, []);

    const handleSampleTypeAddClick = async () => {
        if(selectedSampleType){
            setService((prev) => {
                const isAlreadyAdded = prev?.sample_types?.some(
                    (sampleType) => sampleType.id === selectedSampleType?.id
                );
                if (isAlreadyAdded) {
                    showAlert("Already registered")
                    return prev;
                }else{
                    setSearchSampleType('')
                    return {
                        ...prev,
                        sample_types: [
                            ...(prev?.sample_types || []),
                            {
                                id: selectedSampleType?.id,
                                name: selectedSampleType?.label
                            }
                        ]
                    };
                }
            });
        }else{
            showAlert('No options selected.');
        }

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
        if(selectedExtension){
            setService((prev) => {
                const isAlreadyAdded = prev?.extensions?.some(
                    (extension) => extension.id === selectedExtension?.id
                );
                if (isAlreadyAdded) {
                    showAlert("Already registered")
                    return prev;
                }else{
                    setSearchExtension('')
                    return {
                        ...prev,
                        extensions: [
                            ...(prev?.extensions || [] ),
                            {
                                id: selectedExtension?.id,
                                name: selectedExtension?.label,
                                required: required.value
                            }
                        ]
                    };
                }
            });
        }else{
            showAlert('No options selected.');
        }
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
                        value={service?.type}
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
                            <SearchSelectBox
                                options={sampleTypeOptions}
                                onSelect={setSelectedSampleType}
                                placeholder={'Sample Type name...'}
                                value={searchSampleType}
                                onChange={setSearchSampleType}
                            />
                            <BlueButton
                                name={'Add'}
                                onClick={handleSampleTypeAddClick}
                                />
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
                                            onClick={() => handleSampleTypeDeleteClick(sampleType.id!)}
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
                            <SearchSelectBox
                                options={extensionOptions}
                                onSelect={setSelectedExtension}
                                placeholder={'Extension name...'}
                                value={searchExtension}
                                onChange={setSearchExtension}
                            />
                            <SelectBox
                                value={required.name}
                                options={requiredOption}
                                label={""}
                                onChange={(selectedOption) => {
                                    setRequired(
                                        {
                                            name: selectedOption.name,
                                            value: selectedOption.value
                                        } as SelectBoxOption
                                    )
                                }}
                            />
                            <BlueButton
                                name={'Add'}
                                onClick={handleExtensionAddClick}
                            />
                            {/*<SelectSearchBox type={'extension'} onSelect={setSelectedExtension} width={'220px'}/>

                            <button className={style.addButton} onClick={() => handleExtensionAddClick()}>Add</button>*/}
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