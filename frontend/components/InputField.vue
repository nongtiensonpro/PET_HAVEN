<template>
  <div>
    <label :for="name">{{ label }}</label>
    <input :id="name" v-bind="$attrs" v-model="fieldValue" @blur="onBlur" />
    <span v-if="errors && errors.length">{{ errors }}</span>
  </div>
</template>

<script>
import { useField } from 'vee-validate';
import * as yup from 'yup';

export default {
  name: 'InputField',
  props: {
    name: String,
    label: String,
    rules: String
  },
  setup(props) {
    const { value: fieldValue, errorMessage: errors, handleBlur: onBlur } = useField(
        props.name,
        yup.string().required('Trường này không được để trống')
    );
    return {
      fieldValue,
      errors,
      onBlur
    };
  }
};
</script>

<style scoped>
span {
  color: red;
}
</style>
