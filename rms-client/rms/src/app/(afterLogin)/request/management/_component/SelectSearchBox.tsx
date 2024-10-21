import style from "@/app/(afterLogin)/request/management/_component/selectSearchBox.module.css";
import React, {useEffect, useRef, useState} from "react";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {postSampleTypes} from "@/app/(afterLogin)/request/management/service/_api/postSampleTypes";
import {SampleType} from "@/model/SampleType";
import {postExtensions} from "@/app/(afterLogin)/request/management/service/_api/postExtensions";
import {postServices} from "@/app/(afterLogin)/request/management/_api/postServices";
import {Query} from "@/model/Query";

interface Props {
    type: 'sampleType' | 'extension' | 'service';
    onSelect: (option: SelectBoxOption) => void;
    width?: string;
}

export default function SelectSearchBox({ type, onSelect, width }: Props) {
    const [selectedValue, setSelectedValue] = useState<string>('');
    const [isOpen, setIsOpen] = useState<boolean>(false)
    const [options, setOptions] = useState<SelectBoxOption[]>([]);
    const selectBoxRef = useRef<HTMLDivElement>(null);

    const transformDataToOptions = (data: SampleType[]): SelectBoxOption[] => {
        if (!data) {
            return [];
        }
        return data.map(value => ({
            value: value.id,
            name: value.name
        }));
    };

    const toggleList = () => {
        setIsOpen(!isOpen)
    }

    const handleOptionClick = (option: SelectBoxOption) => {
        setSelectedValue(option.name!);
        setIsOpen(!isOpen);
        onSelect(option);
    };

    const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setSelectedValue(event.target.value);
    };

    const fetchOptions = async () => {
        setOptions([]);
        const sampleTypeQuery: Query = {
            filter_groups:[
                {
                    condition_type: "OR",
                    filters: [
                        {
                            table: "sample_type",
                            column: "id",
                            value: selectedValue,
                            operator: "LIKE"
                        },
                        {
                            table: "sample_type",
                            column: "name",
                            value: selectedValue,
                            operator: "LIKE"
                        }
                    ]
                }
            ]
        }
        const extensionQuery: Query = {
            filter_groups:[
                {
                    condition_type: "OR",
                    filters: [
                        {
                            table: "extension",
                            column: "id",
                            value: selectedValue,
                            operator: "LIKE"
                        },
                        {
                            table: "extension",
                            column: "name",
                            value: selectedValue,
                            operator: "LIKE"
                        }
                    ]
                }
            ]
        }
        const serviceQuery: Query = {
            filter_groups:[
                {
                    condition_type: "OR",
                    filters: [
                        {
                            table: "service",
                            column: "id",
                            value: selectedValue,
                            operator: "LIKE"
                        },
                        {
                            table: "service",
                            column: "name",
                            value: selectedValue,
                            operator: "LIKE"
                        }
                    ]
                }
            ]
        }
        let response;

        switch (type) {
            case 'sampleType' :
                response = await postSampleTypes(sampleTypeQuery);
                break;
            case 'extension' :
                response = await postExtensions(extensionQuery);
                break;
            case 'service' :
                response = await postServices(serviceQuery);
                break;
            default:
                return;
        }

        const data = await response.json();
        setOptions(transformDataToOptions(data));
    };

    const handleClickOutside = (event: MouseEvent) => {
        if (selectBoxRef.current && !selectBoxRef.current.contains(event.target as Node)) {
            setIsOpen(false);
        }
    };

    useEffect(() => {
        fetchOptions();
    }, [selectedValue]);

    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return (
        <div ref={selectBoxRef} className={style.container} style={{ width: width }} onClick={toggleList}>
            <section className={style.selectSection} >
                <div className={`${style.btnSelect} ${isOpen ? style.open : ''}`}>
                    <input
                        className={style.selectInput}
                        type="text"
                        value={selectedValue}
                        onChange={handleSearchChange}
                    />
                    <FontAwesomeIcon icon={faChevronDown}/>
                </div>
                <div className={`${style.searchList} ${isOpen ? style.open : ''}`} style={{width: `calc(${width} + 2vw)`}}>
                    <ul className={style.listMember}>
                        {options.map((option) => (
                            <li key={option.name}>
                                <button onClick={() => handleOptionClick(option)} style={{width}}>
                                    {option.value} / {option.name}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </section>
        </div>
    )
}