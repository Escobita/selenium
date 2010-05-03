/**
 * @fileoverview Atoms for simulating user actions against the DOM.
 * The bot.action namespace is required since these atoms would otherwise form a
 * circular dependency between bot.dom and bot.events.
 *
 * @author jmleyba@gmail.com (Jason Leyba)
 */

goog.provide('bot.action');

goog.require('bot.Error');
goog.require('bot.ErrorCode');
goog.require('bot.dom');
goog.require('bot.events');
goog.require('goog.dom');
goog.require('goog.dom.TagName');



/**
 * Throws an error if an element is not currently displayed.
 * @param {Element} element The element to check.
 * @see bot.dom.isDisplayed
 */
bot.action.checkDisplayed_ = function(element) {
  if (!bot.dom.isDisplayed(element)) {
    throw new bot.Error(bot.ErrorCode.ELEMENT_NOT_VISIBLE,
        'Element is not currently visible and may not be manipuated');
  }
};


/**
 * Simulates a user "selecting" an element. Only elements that support the
 * "checked" or "selected" attribute may be selected. This function has no
 * effect if the element is already in the desired state.
 *
 * @param {Element} element The element to manipulate.
 * @param {boolean} selected Whether the final state of the element should be
 *     what a user would consider "selected".
 */
bot.action.setSelected = function(element, selected) {
  if (bot.dom.isInHead(element)) {
    throw new bot.Error(bot.ErrorCode.ELEMENT_NOT_SELECTABLE,
        'You may not select children of HEAD');
  }

  if (element.disabled) {
    throw new bot.Error(bot.ErrorCode.INVALID_ELEMENT_STATE,
        'You may not select a disabled element');
  }

  switch (element.tagName) {
    case goog.dom.TagName.INPUT:
      bot.action.checkDisplayed_(element);

      if (element.type == 'checkbox' || element.type == 'radio') {
        if (element.checked != selected) {
          if (element.type == 'radio' && !selected) {
            throw new bot.Error(bot.ErrorCode.INVALID_ELEMENT_STATE,
                'You may not deselect a radio button');
          }

          if (selected == bot.dom.isSelected(element)) {
            return;  // Already in the desired state.
          }

          element.checked = selected;
          bot.events.fire(element, 'change');
        }
      } else {
        throw new bot.Error(bot.ErrorCode.ELEMENT_NOT_SELECTABLE,
            'You may not select an unselectable input element: ' +
                element.type);
      }
      break;

    case goog.dom.TagName.OPTION:
      var select = goog.dom.getAncestor(element, function(node) {
        return node.nodeType == goog.dom.NodeType.ELEMENT &&
            node.tagName == goog.dom.TagName.SELECT;
      });

      if (!select) {
        throw new bot.Error(bot.ErrorCode.ELEMENT_NOT_SELECTABLE,
            'You may not de/select an option that is not within a ' +
            'select element');
      }

      if (select.disabled) {
        throw new bot.Error(bot.ErrorCode.INVALID_ELEMENT_STATE,
            'You may not select an option from a disabled select');
      }

      // We check if the parent select is displayed since an option may not be
      // considered displayed if it is not the selected option.
      bot.action.checkDisplayed_(select);

      if (!select.multiple && !selected) {
        throw new bot.Error(bot.ErrorCode.ELEMENT_NOT_SELECTABLE,
            'You may not deselect an option within a select that ' +
            'does not support multiple selections.')
      }

      if (selected == bot.dom.isSelected(element)) {
        return;  // Already in the desired state.
      }

      element.selected = selected;
      bot.events.fire(select, 'change');
      break;

    default:
      throw new bot.Error(bot.ErrorCode.ELEMENT_NOT_SELECTABLE,
          'You may not select an unselectable element: ' + element.tagName);
  }
};


/**
 * Toggles the selected state of the given element.
 *
 * @param {Element} element The element to toggle.
 * @see bot.action.setSelected
 * @see bot.dom.isSelected
 */
bot.action.toggle = function(element) {
  if (element.tagName == goog.dom.TagName.INPUT && 'radio' == element.type) {
    throw new bot.Error(bot.ErrorCode.INVALID_ELEMENT_STATE,
        'You may not toggle a radio button');
  }
  bot.action.setSelected(element, !bot.dom.isSelected(element));
  return bot.dom.isSelected(element);
};
