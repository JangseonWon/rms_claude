import style from "@/app/(afterLogin)/request/management/service/_component/selectSearchBox.module.css";
import React, {useEffect, useRef, useState} from "react";
import {faChevronDown} from "@fortawesome/free-solid-svg-icons";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {SelectBoxOption} from "@/model/SelectBoxOption";
import {getSampleTypes} from "@/app/(afterLogin)/request/management/service/_api/getSampleTypes";
import {Filter} from "@/model/Filter";
import {SampleType} from "@/model/SampleType";
import {getExtensions} from "@/app/(afterLogin)/request/management/service/_api/getExtensions";

interface Props {
    type: 'sampleType' | 'extension';
    onSelect: (option: SelectBoxOption) => void;
}

export default function SelectSearchBox({ type, onSelect }: Props) {
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

    const fetchSampleTypes = async () => {
        setOptions([]);
        const filter: Filter = { value: selectedValue };
        let response;
        if (type === 'sampleType') {
            response = await getSampleTypes(filter);
        } else {
            response = await getExtensions(filter);
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
        fetchSampleTypes();
    }, [selectedValue]);

    useEffect(() => {
        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    return (
        <div ref={selectBoxRef}>
            <section className={style.selectSection}>
                <p className={style.label}>{type === 'sampleType' ? 'SampleType' : 'Extension'} </p>
                <button className={`${style.btnSelect} ${isOpen ? style.open : ''}`} onClick={toggleList}>
                    <div>{selectedValue}</div>
                    <FontAwesomeIcon icon={faChevronDown}/>
                </button>
                <ul className={`${style.listMember} ${isOpen ? style.open : ''}`}>
                    <input
                        type="text"
                        value={selectedValue}
                        onChange={handleSearchChange}
                        className={style.searchInput}
                        placeholder="Search..."
                    />
                    {options.map((option) => (
                        <li key={option.name}>
                            <button onClick={() => handleOptionClick(option)}>
                                {option.value} / {option.name}
                            </button>
                        </li>
                    ))}
                </ul>
            </section>
        </div>
    )
}