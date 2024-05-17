'use client'

import * as React from 'react';
import {useEffect} from 'react';
import {Select as BaseSelect, SelectProps, SelectRootSlotProps,} from '@mui/base/Select';
import {Option as BaseOption, optionClasses} from '@mui/base/Option';
import {styled} from '@mui/system';
import UnfoldMoreRoundedIcon from '@mui/icons-material/UnfoldMoreRounded';
import {
    StyledButton,
    StyledListBox,
    StyledPopup
} from "@/app/(afterLogin)/request/services/precision-oncology/_component/SelectStyle";
import {fetchOrganization} from "@/app/(afterLogin)/request/services/precision-oncology/_api/fetchOrganization";
import {
    useOrganization,
    useOrganizationsAction,
    useSelectOrganization,
    useSelectOrganizationAction
} from "@/app/(afterLogin)/request/services/precision-oncology/store/useOrganizationStore";
import {useLoginUser} from "@/store/LoginUser";

export default function SelectOrganization() {
    const organization = useOrganization();
    const selectOrganization = useSelectOrganization();
    const setOrganizations = useOrganizationsAction();
    const setSelectOrganization = useSelectOrganizationAction();
    const userId = useLoginUser();

    useEffect(() => {
        fetchOrganization(userId)
            .then((data) => {
                setOrganizations(data)
            })
            .catch((error) => {
                console.error('Error fetching organization:', error);
            });
    }, [userId]);

    return (
        <div>
            <Select
                value={selectOrganization}
                onChange={(event, newValue) => setSelectOrganization(newValue)}
            >
                {Array.isArray(organization) && organization.map((c) => (
                    <Option key={c.id} value={c}>
                        {c.name}
                    </Option>
                ))}
            </Select>
        </div>
    );
}

function Select<TValue extends {}, Multiple extends boolean = false>(
    props: SelectProps<TValue, Multiple>,
) {
    const slots: SelectProps<TValue, Multiple>['slots'] = {
        root: Button,
        listbox: StyledListBox,
        popup: StyledPopup,
        ...props.slots,
    };

    return <BaseSelect {...props} slots={slots} />;
}

const Button = React.forwardRef(function Button<
    TValue extends {},
    Multiple extends boolean,
>(
    props: SelectRootSlotProps<TValue, Multiple>,
    ref: React.ForwardedRef<HTMLButtonElement>,
) {
    const { ownerState, ...other } = props;
    return (
        <StyledButton type="button" {...other} ref={ref}>
            {other.children}
            <UnfoldMoreRoundedIcon />
        </StyledButton>
    );
});

const Option = styled(BaseOption)(
    ({ theme }) => `
  list-style: none;
  padding: 8px;
  border-radius: 8px;
  cursor: default;

  &:last-of-type {
    border-bottom: none;
  }

  &.${optionClasses.selected} {
    background-color: ${theme.palette.mode === 'dark' ? '#003A75' : '#DAECFF'};
    color: ${theme.palette.mode === 'dark' ? '#DAECFF' : '#003A75'};
  }

  &.${optionClasses.highlighted} {
    background-color: ${theme.palette.mode === 'dark' ? '#303740' : '#E5EAF2'};
    color: ${theme.palette.mode === 'dark' ? '#C7D0DD' : '#1C2025'};
  }

  &:focus-visible {
    outline: 3px solid ${theme.palette.mode === 'dark' ? '#0072E5' : '#99CCF3'};
  }

  &.${optionClasses.highlighted}.${optionClasses.selected} {
    background-color: ${theme.palette.mode === 'dark' ? '#003A75' : '#DAECFF'};
    color: ${theme.palette.mode === 'dark' ? '#DAECFF' : '#003A75'};
  }

  &.${optionClasses.disabled} {
    color: ${theme.palette.mode === 'dark' ? '#434D5B' : '#B0B8C4'};
  }

  &:hover:not(.${optionClasses.disabled}) {
    background-color: ${theme.palette.mode === 'dark' ? '#303740' : '#E5EAF2'};
    color: ${theme.palette.mode === 'dark' ? '#C7D0DD' : '#1C2025'};
  }
  `,
);
